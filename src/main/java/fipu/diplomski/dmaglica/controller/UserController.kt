package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.model.data.NotificationOptions
import fipu.diplomski.dmaglica.model.data.User
import fipu.diplomski.dmaglica.model.data.UserLocation
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.model.response.DataResponse
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.service.UserService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for managing user accounts and related operations.
 *
 * This controller handles all user-related operations including:
 * - Account creation and authentication
 * - Profile management (email, username, password updates)
 * - Notification preferences management
 * - Location data handling
 * - Account deletion
 *
 * All endpoints follow REST conventions and return appropriate response types:
 * - [DataResponse] for operations returning data payloads
 * - [BasicResponse] for simple status responses
 *
 * @note Security notes:
 * - All sensitive operations require proper authentication
 * - Passwords are never returned in responses
 * - Location data access requires proper permissions
 *
 * @see UserService for business logic implementation
 * @see UserEntity for user data model details
 * @see NotificationOptions for notification model details
 * @see UserLocation for user location model details
 */
@RestController
@RequestMapping(Paths.USER)
class UserController(private val userService: UserService) {

    /**
     * Registers a new user account.
     *
     * @param email The user's email address (must be unique)
     * @param username The desired username
     * @param password The user's password (will be hashed before storage)
     * @return [DataResponse] containing:
     *         - success: Boolean indicating operation status
     *         - message: Descriptive status message
     *         - data: The created userId if successful, null otherwise
     *
     * Registration process:
     * 1. Validates if email isn't already registered
     * 2. Hashes the provided password
     * 3. Creates user with default USER role
     * 4. Initializes notification options with all disabled
     * 5. Persists both user and notification settings
     *
     * Example request:
     * POST /api/auth/signup?email=user@example.com&username=newuser&password=secure123
     *
     * @note Passwords are hashed using BCrypt before storage
     * @note Notification options are initialized with all services disabled
     *
     */
    @PostMapping(Paths.SIGNUP)
    fun signup(
        @RequestParam("email") email: String,
        @RequestParam("username") username: String,
        @RequestParam("password") password: String
    ): DataResponse<Int> = userService.signup(email, username, password)

    /**
     * Authenticates a user and initiates a login session.
     *
     * @param email The user's registered email address
     * @param password The user's password (will be verified against stored hash)
     * @return [DataResponse] containing:
     *         - success: Boolean indicating authentication status
     *         - message: Descriptive status message
     *         - data: The authenticated userId if successful, null otherwise
     *
     * Authentication process:
     * 1. Verifies email exists in system
     * 2. Validates password matches stored hash
     * 3. Returns user data if credentials are valid
     *
     * Example request:
     * GET /api/auth/login?email=user@example.com&password=secure123
     *
     * @note Uses secure password hash comparison
     * @note Does not return the hashed password in the response
     */
    @GetMapping(Paths.LOGIN)
    fun login(
        @RequestParam("email") email: String,
        @RequestParam("password") password: String
    ): DataResponse<Int> = userService.login(email, password)

    /**
     * Retrieves a user's notification preferences and settings.
     *
     * @param userId The id of the user whose notification options to fetch
     * @return [NotificationOptions] containing the user's preferences if found, null if:
     *         - User doesn't exist
     *         - Notification options aren't configured for user
     *
     * The returned options include:
     * - Push notification enabled state
     * - Email notification enabled state
     * - Location services enabled state
     *
     * Example request:
     * GET /api/users/notification-options?userId=123
     *
     * @note Notification options are automatically created with all services disabled during user registration
     */
    @GetMapping(Paths.GET_NOTIFICATION_OPTIONS)
    fun getUserNotificationOptions(@RequestParam("userId") userId: Int): NotificationOptions? =
        userService.getNotificationOptions(userId)

    /**
     * Retrieves a user's last known geographical coordinates.
     *
     * @param userId The id of the user whose location to fetch
     * @return [UserLocation] containing latitude/longitude if available, null if:
     *         - User doesn't exist
     *         - Either latitude or longitude is not set
     *
     * Example request:
     * GET /api/users/location?userId=123
     *
     * @note Coordinates are in WGS84 decimal degree format
     * @warning Location data may be outdated if user hasn't recently updated their position
     */
    @GetMapping(Paths.GET_LOCATION)
    fun getUserLocation(@RequestParam("userId") userId: Int): UserLocation? = userService.getLocation(userId)

    /**
     * Updates a user's email address.
     *
     * @param userId The id of the user to update
     * @param newEmail The new email address to set (must be non-empty and unique)
     * @return [BasicResponse] indicating operation status with success flag and message
     *
     * Update process:
     * 1. Validates if new email is non-empty
     * 2. Checks if email isn't already registered
     * 3. Updates user record if user is found
     *
     * Example request:
     * PATCH /api/users/email?userId=123&newEmail=new@example.com
     *
     * @note Will affect all future authentication attempts
     */
    @PatchMapping(Paths.UPDATE_EMAIL)
    fun updateUserEmail(
        @RequestParam("userId") userId: Int,
        @RequestParam("newEmail") newEmail: String
    ): BasicResponse = userService.updateEmail(userId, newEmail)

    /**
     * Updates a user's username.
     *
     * @param userId The id of the user to update
     * @param newUsername The new username to set (must be non-empty)
     * @return [BasicResponse] indicating operation status
     *
     * Update process:
     * 1. Validates if new username is non-empty
     * 3. Updates user record if user is found
     *
     * Example request:
     * PATCH /api/users/username?userId=123&newUsername=newhandle
     *
     */
    @PatchMapping(Paths.UPDATE_USERNAME)
    fun updateUserUsername(
        @RequestParam("userId") userId: Int,
        @RequestParam("newUsername") newUsername: String
    ): BasicResponse = userService.updateUsername(userId, newUsername)

    /**
     * Updates a user's password.
     *
     * @param userId The id of the user to update
     * @param newPassword The new password to set (must be non-empty)
     * @return [BasicResponse] indicating operation status
     *
     * Security process:
     * 1. Validates if new password is non-empty
     * 2. Hashes password using BCrypt before storage
     * 3. Updates user record with new hash
     *
     * Example request:
     * PATCH /api/users/password?userId=123&newPassword=NewSecurePassword123
     *
     * @note Password is immediately hashed and never stored in plaintext
     */
    @PatchMapping(Paths.UPDATE_PASSWORD)
    fun updateUserPassword(
        @RequestParam("userId") userId: Int,
        @RequestParam("newPassword") newPassword: String
    ): BasicResponse = userService.updatePassword(userId, newPassword)

    /**
     * Updates a user's notification preferences and service settings.
     *
     * @param userId The id of the user to update
     * @param pushNotificationsTurnedOn Whether push notifications should be enabled
     * @param emailNotificationsTurnedOn Whether email notifications should be enabled
     * @param locationServicesTurnedOn Whether location services should be enabled
     * @return [BasicResponse] indicating operation status with success flag and message
     *
     * Update process:
     * 1. Verifies if user exists
     * 2. Updates all specified notification settings
     * 3. Persists changes to database
     *
     * Example request:
     * PATCH /api/users/notification-options?
     *   userId=123&
     *   pushNotificationsTurnedOn=true&
     *   emailNotificationsTurnedOn=false&
     *   locationServicesTurnedOn=true
     *
     * @note Changes take effect immediately
     */
    @PatchMapping(Paths.UPDATE_NOTIFICATION_OPTIONS)
    fun updateUserNotificationOptions(
        @RequestParam("userId") userId: Int,
        @RequestParam("pushNotificationsTurnedOn") pushNotificationsTurnedOn: Boolean,
        @RequestParam("emailNotificationsTurnedOn") emailNotificationsTurnedOn: Boolean,
        @RequestParam("locationServicesTurnedOn") locationServicesTurnedOn: Boolean
    ): BasicResponse = userService.updateNotificationOptions(
        userId,
        pushNotificationsTurnedOn,
        emailNotificationsTurnedOn,
        locationServicesTurnedOn
    )

    /**
     * Updates a user's last known geographical coordinates.
     *
     * @param userId The id of the user whose location to update
     * @param latitude The new latitude coordinate (decimal degrees between -90 and 90)
     * @param longitude The new longitude coordinate (decimal degrees between -180 and 180)
     * @return [BasicResponse] with:
     *         - success: true if update was successful
     *         - message: Operation status message
     *
     * Update process:
     * 1. Validates if user exists
     * 2. Updates both latitude and longitude coordinates
     * 3. Persists changes to database
     *
     * Example request:
     * PATCH /api/users/location?userId=123&latitude=40.7128&longitude=-74.0060
     *
     * @note Coordinates are stored in WGS84 decimal degree format
     */
    @PatchMapping(Paths.UPDATE_LOCATION)
    fun updateUserLocation(
        @RequestParam("userId") userId: Int,
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double
    ): BasicResponse = userService.updateLocation(userId, latitude, longitude)

    /**
     * Deletes a user by their id.
     *
     * @param userId The id of the user to delete
     * @return [BasicResponse] with:
     *         - success: true if deletion was successful
     *         - message: Operation status message
     *
     * Deletion process:
     * 1. Verifies user exists
     * 2. Attempts to delete user record
     * 3. Returns appropriate status message
     *
     * Example request:
     * DELETE /api/users?userId=123
     *
     * @warning This operation is permanent and cannot be undone
     */
    @DeleteMapping(Paths.DELETE_USER)
    fun deleteUser(@RequestParam("userId") userId: Int): BasicResponse = userService.delete(userId)

    /**
     * Retrieves user details by id.
     *
     * @param userId The id of the user to retrieve
     * @return [User] object if found, null otherwise
     *
     * The returned user object contains:
     * - Basic user information
     * - Notification preferences
     * - Role information
     * - Last known location (if available)
     *
     * Example request:
     * GET /api/users?userId=123
     *
     */
    @GetMapping(Paths.GET_USER)
    fun getUser(@RequestParam("userId") userId: Int): User? = userService.getUser(userId)

}
