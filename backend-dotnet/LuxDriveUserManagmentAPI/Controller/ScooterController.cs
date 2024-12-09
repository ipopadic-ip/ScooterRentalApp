using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using LuxDriveUserManagmentAPI.Data;
using LuxDriveUserManagmentAPI.Models;

namespace LuxDriveUserManagmentAPI.Controllers
{
    [Route("api/scooters")]
    [ApiController]
    [Authorize(Policy = "AdminPolicy")] // Only Admin users can access
    public class ScooterController : ControllerBase
    {
        private readonly LuxDriveDbContext _context;

        public ScooterController(LuxDriveDbContext context)
        {
            _context = context;
        }

        // POST: api/scooters
        [HttpPost]
        public IActionResult AddScooter([FromBody] Scooter scooter)
        {
            if (scooter == null)
                return BadRequest("Scooter data is required.");

            var validTypes = new[] { "Green", "Yellow", "Blue" };
            if (!validTypes.Contains(scooter.ScooterType))
                return BadRequest("Invalid scooter type.");

            _context.Scooters.Add(scooter);
            _context.SaveChanges();

            return CreatedAtAction(nameof(GetScooterById), new { id = scooter.ScooterId }, scooter);
        }

        // DELETE: api/scooters/{id}
        [HttpDelete("{id}")]
        public IActionResult RemoveScooter(int id)
        {
            var scooter = _context.Scooters.FirstOrDefault(s => s.ScooterId == id);

            if (scooter == null)
                return NotFound("Scooter not found.");

            _context.Scooters.Remove(scooter);
            _context.SaveChanges();

            return NoContent();
        }

        // GET: api/scooters/{id}
        [HttpGet("{id}")]
        public IActionResult GetScooterById(int id)
        {
            var scooter = _context.Scooters.FirstOrDefault(s => s.ScooterId == id);

            if (scooter == null)
                return NotFound("Scooter not found.");

            return Ok(scooter);
        }

        // GET: api/scooters
        [HttpGet]
        public IActionResult GetAllScooters()
        {
            var scooters = _context.Scooters
               .Select(s => new { s.ScooterId, s.ScooterType })
               .ToList();
            return Ok(scooters);
            //var scooters = _context.Scooters.ToList();
            //return Ok(scooters);
        }
    }
}
