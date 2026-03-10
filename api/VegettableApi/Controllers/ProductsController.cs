using Microsoft.AspNetCore.Mvc;
using VegettableApi.Models;

namespace VegettableApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ProductsController : ControllerBase
{
    private static readonly List<ProductSummary> _products = new()
    {
        new ProductSummary { CropCode = "LA1", CropName = "高麗菜", Category = "葉菜類", AveragePrice = 35.5, PriceChange = -2.1, PriceChangePercent = -5.5, Trend = "down", ImageUrl = "https://picsum.photos/seed/cabbage/200" },
        new ProductSummary { CropCode = "FB1", CropName = "花椰菜", Category = "花果類", AveragePrice = 45.0, PriceChange = 1.5, PriceChangePercent = 3.4, Trend = "up", ImageUrl = "https://picsum.photos/seed/cauliflower/200" },
        new ProductSummary { CropCode = "SA1", CropName = "蘿蔔", Category = "根莖類", AveragePrice = 20.0, PriceChange = 0, PriceChangePercent = 0, Trend = "stable", ImageUrl = "https://picsum.photos/seed/radish/200" },
        new ProductSummary { CropCode = "FJ1", CropName = "番茄", Category = "花果類", AveragePrice = 60.5, PriceChange = 5.0, PriceChangePercent = 9.0, Trend = "up", ImageUrl = "https://picsum.photos/seed/tomato/200" },
        new ProductSummary { CropCode = "SE1", CropName = "青蔥", Category = "辛香料", AveragePrice = 120.0, PriceChange = -10.0, PriceChangePercent = -7.6, Trend = "down", ImageUrl = "https://picsum.photos/seed/scallion/200" }
    };

    [HttpGet]
    public ActionResult<ApiResponse<List<ProductSummary>>> GetProducts([FromQuery] string? category)
    {
        var query = _products.AsQueryable();
        if (!string.IsNullOrEmpty(category))
        {
            query = query.Where(p => p.Category == category);
        }
        return Ok(ApiResponse<List<ProductSummary>>.Ok(query.ToList()));
    }

    [HttpGet("paginated")]
    public ActionResult<ApiResponse<PaginatedResponse<ProductSummary>>> GetProductsPaginated([FromQuery] string? category, [FromQuery] int offset = 0, [FromQuery] int limit = 10)
    {
        var query = _products.AsQueryable();
        if (!string.IsNullOrEmpty(category))
        {
            query = query.Where(p => p.Category == category);
        }

        var total = query.Count();
        var items = query.Skip(offset).Take(limit).ToList();

        var response = new PaginatedResponse<ProductSummary>
        {
            Items = items,
            Total = total,
            Offset = offset,
            Limit = limit
        };

        return Ok(ApiResponse<PaginatedResponse<ProductSummary>>.Ok(response));
    }

    [HttpGet("search")]
    public ActionResult<ApiResponse<List<ProductSummary>>> SearchProducts([FromQuery] string? keyword)
    {
        var query = _products.AsQueryable();
        if (!string.IsNullOrEmpty(keyword))
        {
            query = query.Where(p => p.CropName.Contains(keyword) || p.CropCode.Contains(keyword));
        }
        return Ok(ApiResponse<List<ProductSummary>>.Ok(query.ToList()));
    }

    [HttpGet("search/paginated")]
    public ActionResult<ApiResponse<PaginatedResponse<ProductSummary>>> SearchProductsPaginated([FromQuery] string? keyword, [FromQuery] int offset = 0, [FromQuery] int limit = 10)
    {
        var query = _products.AsQueryable();
        if (!string.IsNullOrEmpty(keyword))
        {
            query = query.Where(p => p.CropName.Contains(keyword) || p.CropCode.Contains(keyword));
        }

        var total = query.Count();
        var items = query.Skip(offset).Take(limit).ToList();

        var response = new PaginatedResponse<ProductSummary>
        {
            Items = items,
            Total = total,
            Offset = offset,
            Limit = limit
        };

        return Ok(ApiResponse<PaginatedResponse<ProductSummary>>.Ok(response));
    }

    [HttpGet("{cropName}")]
    public ActionResult<ApiResponse<ProductDetail>> GetProductDetail(string cropName)
    {
        var summary = _products.FirstOrDefault(p => p.CropName == cropName);
        if (summary == null)
        {
            return NotFound(ApiResponse<ProductDetail>.Error("Product not found"));
        }

        var detail = new ProductDetail
        {
            CropCode = summary.CropCode,
            CropName = summary.CropName,
            Category = summary.Category,
            CurrentPrice = summary.AveragePrice,
            HighestPrice = summary.AveragePrice * 1.2,
            LowestPrice = summary.AveragePrice * 0.8,
            Volume = 15000,
            Description = $"{summary.CropName}是常見的{summary.Category}，富含營養價值。",
            ImageUrl = summary.ImageUrl,
            RecentPrices = new List<DailyPrice>
            {
                new DailyPrice { Date = DateTime.Now.AddDays(-6).ToString("MM-dd"), Price = summary.AveragePrice * 0.95, Volume = 14000 },
                new DailyPrice { Date = DateTime.Now.AddDays(-5).ToString("MM-dd"), Price = summary.AveragePrice * 0.98, Volume = 14500 },
                new DailyPrice { Date = DateTime.Now.AddDays(-4).ToString("MM-dd"), Price = summary.AveragePrice * 1.05, Volume = 16000 },
                new DailyPrice { Date = DateTime.Now.AddDays(-3).ToString("MM-dd"), Price = summary.AveragePrice * 1.02, Volume = 15500 },
                new DailyPrice { Date = DateTime.Now.AddDays(-2).ToString("MM-dd"), Price = summary.AveragePrice * 0.99, Volume = 14800 },
                new DailyPrice { Date = DateTime.Now.AddDays(-1).ToString("MM-dd"), Price = summary.AveragePrice * 1.01, Volume = 15200 },
                new DailyPrice { Date = DateTime.Now.ToString("MM-dd"), Price = summary.AveragePrice, Volume = 15000 }
            },
            MarketPrices = new List<MarketPrice>
            {
                new MarketPrice { MarketName = "台北一", Price = summary.AveragePrice * 1.1, Volume = 5000, Trend = "up" },
                new MarketPrice { MarketName = "台北二", Price = summary.AveragePrice * 1.05, Volume = 4000, Trend = "stable" },
                new MarketPrice { MarketName = "台中", Price = summary.AveragePrice * 0.9, Volume = 3000, Trend = "down" },
                new MarketPrice { MarketName = "高雄", Price = summary.AveragePrice * 0.85, Volume = 3000, Trend = "down" }
            }
        };

        return Ok(ApiResponse<ProductDetail>.Ok(detail));
    }
}
