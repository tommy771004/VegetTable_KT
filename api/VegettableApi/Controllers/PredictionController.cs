using Microsoft.AspNetCore.Mvc;
using VegettableApi.Models;

namespace VegettableApi.Controllers;

[ApiController]
[Route("api/[controller]")]
public class PredictionController : ControllerBase
{
    [HttpGet("{cropName}")]
    public ActionResult<ApiResponse<PricePrediction>> GetPrediction(string cropName)
    {
        var prediction = new PricePrediction
        {
            CropName = cropName,
            CurrentPrice = 35.5,
            PredictedPrice = 38.0,
            Confidence = 0.85,
            Trend = "up",
            Reason = "近期氣候不佳，預期產量減少，價格可能上漲。",
            HistoricalData = new List<MonthlyPrice>
            {
                new MonthlyPrice { Month = "1月", Price = 30.0 },
                new MonthlyPrice { Month = "2月", Price = 32.5 },
                new MonthlyPrice { Month = "3月", Price = 35.5 },
                new MonthlyPrice { Month = "4月", Price = 38.0 },
                new MonthlyPrice { Month = "5月", Price = 40.0 }
            }
        };

        return Ok(ApiResponse<PricePrediction>.Ok(prediction));
    }

    [HttpGet("seasonal")]
    public ActionResult<ApiResponse<List<SeasonalInfo>>> GetSeasonalInfo([FromQuery] string? category)
    {
        var info = new List<SeasonalInfo>
        {
            new SeasonalInfo
            {
                CropName = "高麗菜",
                Category = "葉菜類",
                BestMonths = new List<int> { 11, 12, 1, 2, 3 },
                Description = "冬季高麗菜最為鮮甜爽脆，適合清炒或煮火鍋。",
                ImageUrl = "https://picsum.photos/seed/cabbage/200"
            },
            new SeasonalInfo
            {
                CropName = "白蘿蔔",
                Category = "根莖類",
                BestMonths = new List<int> { 12, 1, 2 },
                Description = "冬季盛產，口感細緻多汁，適合燉湯。",
                ImageUrl = "https://picsum.photos/seed/radish/200"
            }
        };

        if (!string.IsNullOrEmpty(category))
        {
            info = info.Where(i => i.Category == category).ToList();
        }

        return Ok(ApiResponse<List<SeasonalInfo>>.Ok(info));
    }

    [HttpGet("{cropName}/recipes")]
    public ActionResult<ApiResponse<List<Recipe>>> GetRecipes(string cropName)
    {
        var recipes = new List<Recipe>
        {
            new Recipe
            {
                Id = 1,
                Title = $"清炒{cropName}",
                Description = $"簡單快速的{cropName}料理，保留蔬菜原味。",
                ImageUrl = "https://picsum.photos/seed/recipe1/200",
                PrepTime = 15,
                Difficulty = "簡單",
                Ingredients = new List<string> { $"{cropName} 半顆", "蒜頭 3瓣", "鹽巴 適量", "油 1大匙" },
                Instructions = new List<string> { $"將{cropName}洗淨切塊。", "蒜頭切末。", "熱鍋下油，爆香蒜末。", $"加入{cropName}大火快炒。", "加入鹽巴調味，炒熟即可起鍋。" }
            },
            new Recipe
            {
                Id = 2,
                Title = $"{cropName}炒肉絲",
                Description = $"營養均衡的家常菜。",
                ImageUrl = "https://picsum.photos/seed/recipe2/200",
                PrepTime = 20,
                Difficulty = "中等",
                Ingredients = new List<string> { $"{cropName} 1/4顆", "豬肉絲 150g", "醬油 1大匙", "蒜頭 2瓣" },
                Instructions = new List<string> { "豬肉絲用醬油醃製10分鐘。", $"將{cropName}洗淨切塊。", "熱鍋下油，先將肉絲炒至變色後盛起。", $"原鍋爆香蒜末，加入{cropName}拌炒。", "加入少許水悶煮2分鐘。", "加入肉絲拌炒均勻即可。" }
            }
        };

        return Ok(ApiResponse<List<Recipe>>.Ok(recipes));
    }
}
