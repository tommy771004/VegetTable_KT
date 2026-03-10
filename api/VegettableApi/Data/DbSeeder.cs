using VegettableApi.Models;

namespace VegettableApi.Data;

public static class DbSeeder
{
    public static void Seed(AppDbContext context)
    {
        if (!context.PriceAlerts.Any())
        {
            context.PriceAlerts.AddRange(
                new PriceAlert
                {
                    CropName = "高麗菜",
                    TargetPrice = 30.0,
                    Condition = "below",
                    IsActive = true,
                    DeviceToken = "test_device_token",
                    CreatedAt = DateTime.UtcNow
                },
                new PriceAlert
                {
                    CropName = "青江菜",
                    TargetPrice = 50.0,
                    Condition = "above",
                    IsActive = true,
                    DeviceToken = "test_device_token",
                    CreatedAt = DateTime.UtcNow
                }
            );
            context.SaveChanges();
        }
    }
}
