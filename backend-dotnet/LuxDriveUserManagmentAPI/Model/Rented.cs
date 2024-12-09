namespace LuxDriveUserManagmentAPI.Model
{
    public class Rented
    {
        public int RentedId { get; set; } // Primary key
        public int UserId { get; set; } // Foreign key to User table
        public int ScooterId { get; set; } // Foreign key to Scooter table
        public int HoursRequested { get; set; } // Hours rented
        public DateTime StartTime { get; set; } // Rental start time
        public DateTime EndTime { get; set; } // Rental end time
    }
}
