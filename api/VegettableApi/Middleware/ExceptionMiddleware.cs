using System.Net;
using System.Text.Json;
using Microsoft.AspNetCore.Mvc;
using VegettableApi.Models;

namespace VegettableApi.Middleware;

/// <summary>
/// 全域例外處理中介層 (改用標準 RFC 7807 ProblemDetails 格式)
/// </summary>
public class ExceptionMiddleware
{
    private readonly RequestDelegate _next;
    private readonly ILogger<ExceptionMiddleware> _logger;

    public ExceptionMiddleware(RequestDelegate next, ILogger<ExceptionMiddleware> logger)
    {
        _next = next;
        _logger = logger;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await _next(context);
        }
        catch (HttpRequestException ex)
        {
            _logger.LogError(ex, "外部 API 請求失敗: {Method} {Path}", context.Request.Method, context.Request.Path);
            await WriteProblemDetailsAsync(context, HttpStatusCode.BadGateway,
                "農業部資料服務暫時無法連線，請稍後再試", ex.Message);
        }
        catch (TaskCanceledException ex) when (!context.RequestAborted.IsCancellationRequested)
        {
            // 區分逾時 vs 用戶端主動取消
            _logger.LogWarning(ex, "請求逾時: {Method} {Path}", context.Request.Method, context.Request.Path);
            await WriteProblemDetailsAsync(context, HttpStatusCode.GatewayTimeout,
                "農業部資料服務回應逾時，請稍後再試", ex.Message);
        }
        catch (TaskCanceledException)
        {
            // 用戶端主動中斷連線，不需回應
            _logger.LogDebug("用戶端中斷連線: {Method} {Path}", context.Request.Method, context.Request.Path);
        }
        catch (JsonException ex)
        {
            _logger.LogError(ex, "JSON 解析失敗: {Method} {Path}", context.Request.Method, context.Request.Path);
            await WriteProblemDetailsAsync(context, HttpStatusCode.BadGateway,
                "農業部資料格式異常，請稍後再試", ex.Message);
        }
        catch (UnauthorizedAccessException ex)
        {
            _logger.LogWarning(ex, "未授權的存取: {Method} {Path}", context.Request.Method, context.Request.Path);
            await WriteProblemDetailsAsync(context, HttpStatusCode.Unauthorized,
                "未授權的存取", ex.Message);
        }
        catch (KeyNotFoundException ex)
        {
            _logger.LogWarning(ex, "找不到資源: {Method} {Path}", context.Request.Method, context.Request.Path);
            await WriteProblemDetailsAsync(context, HttpStatusCode.NotFound,
                "找不到請求的資源", ex.Message);
        }
        catch (ArgumentException ex)
        {
            _logger.LogWarning(ex, "參數錯誤: {Method} {Path}", context.Request.Method, context.Request.Path);
            await WriteProblemDetailsAsync(context, HttpStatusCode.BadRequest,
                "請求參數錯誤", ex.Message);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "未預期的錯誤: {Method} {Path}", context.Request.Method, context.Request.Path);
            await WriteProblemDetailsAsync(context, HttpStatusCode.InternalServerError,
                "伺服器內部錯誤，請稍後再試", ex.Message);
        }
    }

    private static async Task WriteProblemDetailsAsync(
        HttpContext context, HttpStatusCode statusCode, string title, string detail)
    {
        // 使用標準 ProblemDetails 的 Content-Type
        context.Response.ContentType = "application/problem+json; charset=utf-8";
        context.Response.StatusCode = (int)statusCode;

        var problemDetails = new ProblemDetails
        {
            Status = (int)statusCode,
            Title = title,
            Detail = detail, // 在正式環境中，若不想暴露詳細錯誤，可將此設為 null 或自訂訊息
            Instance = context.Request.Path
        };

        var json = JsonSerializer.Serialize(problemDetails, new JsonSerializerOptions
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
        });
        
        await context.Response.WriteAsync(json);
    }
}
