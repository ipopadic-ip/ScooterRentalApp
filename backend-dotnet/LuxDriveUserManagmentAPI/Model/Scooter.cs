namespace LuxDriveUserManagmentAPI.Models
{
    public class Scooter
    {
        public int ScooterId { get; set; }
        public string ScooterType { get; set; } // "Green", "Yellow", "Blue"
        public bool IsRented { get; set; }
        public int PricePerMin { get; set; }
        public int MaxSpeed { get; set; }
    }

}
