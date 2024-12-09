using Microsoft.AspNetCore.Mvc;
using LuxDriveUserManagmentAPI.Data;
using LuxDriveUserManagmentAPI.Models;
using LuxDriveUserManagmentAPI.Requests;
using Microsoft.EntityFrameworkCore;

using BCrypt.Net;
using LuxDriveUserManagmentAPI.Controller;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace LuxDriveUserManagmentAPI.Controllers
{
    [ApiController]
    [Route("api/users")]
    public class UserController : ControllerBase
    {
        private readonly LuxDriveDbContext _dbContext;
        private readonly IConfiguration _configuration;

        public UserController(LuxDriveDbContext dbContext, IConfiguration configuration)
        {
            _dbContext = dbContext;
            _configuration = configuration;
        }

        // Register a new user
        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterRequest request)
        {
            if (await _dbContext.Users.AnyAsync(u => u.Email == request.Email))
                //return BadRequest("User with this email already exists.");
                return BadRequest(new { message = "User with this email already exists." });

            // Hash the password
            string hashedPassword = BCrypt.Net.BCrypt.HashPassword(request.Password);

            var newUser = new User
            {
                Email = request.Email,
                Password = hashedPassword,
                //UserType = "Regular"
                UserType = request.UserType
            };

            _dbContext.Users.Add(newUser);
            await _dbContext.SaveChangesAsync();

            //return Ok("User registered successfully.");
            return Ok(new { message = "User registered successfully." });
        }

        // Log in an existing user
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            var user = await _dbContext.Users
                .FirstOrDefaultAsync(u => u.Email == request.Email);

            if (user == null || !BCrypt.Net.BCrypt.Verify(request.Password, user.Password))
            {
                return Unauthorized(new { message = "Invalid email or password." });
            }

            var claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.Sub, user.Email),
                new Claim(ClaimTypes.Role, user.UserType),
                new Claim(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString()) // Token identifier
            };

            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["Jwt:Key"]));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var token = new JwtSecurityToken(
                issuer: _configuration["Jwt:Issuer"],
                audience: _configuration["Jwt:Audience"],
                claims: claims,
                expires: DateTime.UtcNow.AddHours(1), // Token expiry
                signingCredentials: creds
            );

            var tokenString = new JwtSecurityTokenHandler().WriteToken(token);

            return Ok(new
            {
                message = "Login successful",
                userType = user.UserType,
                token = tokenString
            });

            //if (user == null)
            //    return Unauthorized(new { message = "Invalid email or password." });

            //if (user.UserType == "Admin")
            //    return Ok(new { message = "Admin logged in successfully.", userType = user.UserType });

            //return Ok(new { message = "User logged in successfully.", userType = user.UserType });

        }

        // Upgrade a Regular user to VIP
        [HttpPost("upgrade-to-vip")]
        public async Task<IActionResult> UpgradeToVip([FromBody] UpgradeRequest request)
        {
            var user = await _dbContext.Users.FirstOrDefaultAsync(u => u.Email == request.Email);

            if (user == null)
                return NotFound("User not found.");

            if (user.UserType != "Regular")
                return Forbid("Only Regular users can upgrade to VIP.");

            user.UserType = "VIP";
            _dbContext.Users.Update(user);
            await _dbContext.SaveChangesAsync();

            return Ok("User upgraded to VIP successfully.");
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteUser(int id)
        {
            // Find the user by ID
            var user = await _dbContext.Users.FindAsync(id);

            // Check if user exists
            if (user == null)
            {
                return NotFound("User not found.");
            }

            // Remove the user from the database
            _dbContext.Users.Remove(user);
            await _dbContext.SaveChangesAsync();

            return Ok("User deleted successfully.");
        }



        // Get all users (Admin only)
        [HttpGet]
        public async Task<IActionResult> GetAllUsers()
        {
            var users = await _dbContext.Users.ToListAsync();
            return Ok(users);
        }
    }

}
