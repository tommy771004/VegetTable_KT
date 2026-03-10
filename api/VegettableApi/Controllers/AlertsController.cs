using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using VegettableApi.Data;
using VegettableApi.Models;

namespace VegettableApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AlertsController : ControllerBase
{
    private readonly AppDbContext _context;

    public AlertsController(AppDbContext context)
    {
        _context = context;
    }

    [HttpGet]
    public async Task<ActionResult<ApiResponse<List<PriceAlert>>>> GetAlerts([FromQuery] string? deviceToken)
    {
        var query = _context.PriceAlerts.AsQueryable();
        if (!string.IsNullOrEmpty(deviceToken))
        {
            query = query.Where(a => a.DeviceToken == deviceToken);
        }
        var alerts = await query.ToListAsync();
        return Ok(ApiResponse<List<PriceAlert>>.Ok(alerts));
    }

    [HttpPost]
    public async Task<ActionResult<ApiResponse<PriceAlert>>> CreateAlert([FromBody] CreateAlertRequest request)
    {
        var alert = new PriceAlert
        {
            CropName = request.CropName,
            TargetPrice = request.TargetPrice,
            Condition = request.Condition,
            DeviceToken = request.DeviceToken,
            IsActive = true,
            CreatedAt = DateTime.UtcNow
        };

        _context.PriceAlerts.Add(alert);
        await _context.SaveChangesAsync();

        return Ok(ApiResponse<PriceAlert>.Ok(alert));
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteAlert(int id, [FromQuery] string? deviceToken)
    {
        var alert = await _context.PriceAlerts.FindAsync(id);
        if (alert == null)
        {
            return NotFound(ApiResponse<object>.Error("Alert not found"));
        }

        if (!string.IsNullOrEmpty(deviceToken) && alert.DeviceToken != deviceToken)
        {
            return Unauthorized(ApiResponse<object>.Error("Unauthorized"));
        }

        _context.PriceAlerts.Remove(alert);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    [HttpPatch("{id}/toggle")]
    public async Task<IActionResult> ToggleAlert(int id, [FromQuery] string? deviceToken)
    {
        var alert = await _context.PriceAlerts.FindAsync(id);
        if (alert == null)
        {
            return NotFound(ApiResponse<object>.Error("Alert not found"));
        }

        if (!string.IsNullOrEmpty(deviceToken) && alert.DeviceToken != deviceToken)
        {
            return Unauthorized(ApiResponse<object>.Error("Unauthorized"));
        }

        alert.IsActive = !alert.IsActive;
        await _context.SaveChangesAsync();

        return NoContent();
    }
}
