using Microsoft.AspNetCore.Mvc;
using VegettableApi.Models;

namespace VegettableApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class MarketsController : ControllerBase
{
    private static readonly List<Market> _markets = new()
    {
        new Market { MarketCode = "104", MarketName = "台北一", Location = "台北市" },
        new Market { MarketCode = "109", MarketName = "台北二", Location = "台北市" },
        new Market { MarketCode = "400", MarketName = "台中", Location = "台中市" },
        new Market { MarketCode = "800", MarketName = "高雄", Location = "高雄市" }
    };

    [HttpGet]
    public ActionResult<ApiResponse<List<Market>>> GetMarkets()
    {
        return Ok(ApiResponse<List<Market>>.Ok(_markets));
    }

    [HttpGet("{marketName}/prices")]
    public ActionResult<ApiResponse<List<MarketPrice>>> GetMarketPrices(string marketName, [FromQuery] string? cropName)
    {
        var prices = new List<MarketPrice>
        {
            new MarketPrice { MarketName = marketName, Price = 35.0, Volume = 5000, Trend = "stable" },
            new MarketPrice { MarketName = marketName, Price = 45.0, Volume = 3000, Trend = "up" }
        };
        return Ok(ApiResponse<List<MarketPrice>>.Ok(prices));
    }

    [HttpGet("compare/{cropName}")]
    public ActionResult<ApiResponse<List<MarketPrice>>> CompareMarketPrices(string cropName, [FromQuery] string? markets)
    {
        var marketList = string.IsNullOrEmpty(markets) 
            ? _markets.Select(m => m.MarketName).ToList() 
            : markets.Split(',').ToList();

        var prices = marketList.Select(m => new MarketPrice
        {
            MarketName = m,
            Price = new Random().Next(20, 60) + new Random().NextDouble(),
            Volume = new Random().Next(1000, 10000),
            Trend = new[] { "up", "down", "stable" }[new Random().Next(3)]
        }).ToList();

        return Ok(ApiResponse<List<MarketPrice>>.Ok(prices));
    }
}
