namespace VegettableApi.Models;

public class ProductSummary
{
    public string CropCode { get; set; } = string.Empty;
    public string CropName { get; set; } = string.Empty;
    public string Category { get; set; } = string.Empty;
    public double AveragePrice { get; set; }
    public double PriceChange { get; set; }
    public double PriceChangePercent { get; set; }
    public string Trend { get; set; } = string.Empty; // "up", "down", "stable"
    public string ImageUrl { get; set; } = string.Empty;
}

public class ProductDetail
{
    public string CropCode { get; set; } = string.Empty;
    public string CropName { get; set; } = string.Empty;
    public string Category { get; set; } = string.Empty;
    public double CurrentPrice { get; set; }
    public double HighestPrice { get; set; }
    public double LowestPrice { get; set; }
    public double Volume { get; set; }
    public string Unit { get; set; } = "公斤";
    public string Description { get; set; } = string.Empty;
    public string ImageUrl { get; set; } = string.Empty;
    public List<DailyPrice> RecentPrices { get; set; } = new();
    public List<MarketPrice> MarketPrices { get; set; } = new();
}

public class DailyPrice
{
    public string Date { get; set; } = string.Empty;
    public double Price { get; set; }
    public double Volume { get; set; }
}

public class Market
{
    public string MarketCode { get; set; } = string.Empty;
    public string MarketName { get; set; } = string.Empty;
    public string Location { get; set; } = string.Empty;
}

public class MarketPrice
{
    public string MarketName { get; set; } = string.Empty;
    public double Price { get; set; }
    public double Volume { get; set; }
    public string Trend { get; set; } = string.Empty;
}

public class PriceAlert
{
    public int Id { get; set; }
    public string CropName { get; set; } = string.Empty;
    public double TargetPrice { get; set; }
    public string Condition { get; set; } = string.Empty; // "above", "below"
    public bool IsActive { get; set; }
    public string DeviceToken { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; }
}

public class CreateAlertRequest
{
    public string CropName { get; set; } = string.Empty;
    public double TargetPrice { get; set; }
    public string Condition { get; set; } = string.Empty;
    public string DeviceToken { get; set; } = string.Empty;
}

public class PricePrediction
{
    public string CropName { get; set; } = string.Empty;
    public double CurrentPrice { get; set; }
    public double PredictedPrice { get; set; }
    public double Confidence { get; set; }
    public string Trend { get; set; } = string.Empty;
    public string Reason { get; set; } = string.Empty;
    public List<MonthlyPrice> HistoricalData { get; set; } = new();
}

public class MonthlyPrice
{
    public string Month { get; set; } = string.Empty;
    public double Price { get; set; }
}

public class SeasonalInfo
{
    public string CropName { get; set; } = string.Empty;
    public string Category { get; set; } = string.Empty;
    public List<int> BestMonths { get; set; } = new();
    public string Description { get; set; } = string.Empty;
    public string ImageUrl { get; set; } = string.Empty;
}

public class Recipe
{
    public int Id { get; set; }
    public string Title { get; set; } = string.Empty;
    public string Description { get; set; } = string.Empty;
    public string ImageUrl { get; set; } = string.Empty;
    public int PrepTime { get; set; } // in minutes
    public string Difficulty { get; set; } = string.Empty;
    public List<string> Ingredients { get; set; } = new();
    public List<string> Instructions { get; set; } = new();
}
