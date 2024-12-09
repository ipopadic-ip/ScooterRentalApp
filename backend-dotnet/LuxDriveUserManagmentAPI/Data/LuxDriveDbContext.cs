using Microsoft.EntityFrameworkCore;
using LuxDriveUserManagmentAPI.Models;
using LuxDriveUserManagmentAPI.Model;

namespace LuxDriveUserManagmentAPI.Data
{
    public class LuxDriveDbContext : DbContext
    {
        public LuxDriveDbContext(DbContextOptions<LuxDriveDbContext> options) : base(options) { }

        // Define the tables for your database
        public DbSet<User> Users { get; set; }
        public DbSet<Scooter> Scooters { get; set; }
        public DbSet<Rented> Rented { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Example: Configure ScooterType as required
            modelBuilder.Entity<Scooter>()
                .Property(s => s.ScooterType)
                .IsRequired();

            modelBuilder.Entity<User>()
                .Property(u => u.UserType)
                .HasDefaultValue("Regular");
        }
    }
}
