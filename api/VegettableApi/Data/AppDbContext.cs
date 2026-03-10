using Microsoft.EntityFrameworkCore;
using VegettableApi.Models;

namespace VegettableApi.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<PriceAlert> PriceAlerts { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        
        modelBuilder.Entity<PriceAlert>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.CropName).IsRequired();
            entity.Property(e => e.TargetPrice).IsRequired();
            entity.Property(e => e.Condition).IsRequired();
            entity.Property(e => e.DeviceToken).IsRequired();
        });
    }
}
