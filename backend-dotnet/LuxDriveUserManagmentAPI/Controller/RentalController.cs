using LuxDriveUserManagmentAPI.Data;
using LuxDriveUserManagmentAPI.Model;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Linq;

namespace LuxDriveUserManagmentAPI.Controller
{
    [ApiController]
    [Route("api/[controller]")]
    public class RentalController : ControllerBase
    {
        private readonly LuxDriveDbContext _context;

        public RentalController(LuxDriveDbContext context)
        {
            _context = context;
        }

        // POST: api/Rental/Rent
        [HttpPost("Rent")]
        public IActionResult RentScooter(int userId, int scooterId, int hoursRequested)
        {
            var scooter = _context.Scooters.FirstOrDefault(s => s.ScooterId == scooterId);
            if (scooter == null || scooter.IsRented)
            {
                return BadRequest("Scooter is not available.");
            }

            var user = _context.Users.FirstOrDefault(u => u.UserId == userId);
            if (user == null)
            {
                return BadRequest("Invalid user.");
            }

            var rental = new Rented
            {
                UserId = userId,
                ScooterId = scooterId,
                HoursRequested = hoursRequested,
                StartTime = DateTime.Now,
                EndTime = DateTime.Now.AddHours(hoursRequested)
            };

            scooter.IsRented = true; // Update scooter status

            _context.Rented.Add(rental);
            _context.SaveChanges();

            return Ok(new
            {
                Message = "Scooter rented successfully.",
                RentalDetails = rental
            });
        }

        // POST: api/Rental/Return
        [HttpPost("Return")]
        public IActionResult ReturnScooter(int rentedId)
        {
            var rental = _context.Rented.FirstOrDefault(r => r.RentedId == rentedId);
            if (rental == null)
            {
                return BadRequest("Invalid rental ID.");
            }

            var scooter = _context.Scooters.FirstOrDefault(s => s.ScooterId == rental.ScooterId);
            if (scooter != null)
            {
                scooter.IsRented = false; // Mark scooter as available
            }

            _context.Rented.Remove(rental);
            _context.SaveChanges();

            return Ok("Scooter returned successfully.");
        }

        // GET: api/Rental/AvailableScooters
        [HttpGet("AvailableScooters")]
        public IActionResult GetAvailableScooters()
        {
            var availableScooters = _context.Scooters.Where(s => !s.IsRented).ToList();

            if (!availableScooters.Any())
            {
                return NotFound("No available scooters found.");
            }

            return Ok(availableScooters);
        }

    }
}
