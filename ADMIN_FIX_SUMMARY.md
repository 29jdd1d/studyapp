# Admin Panel and File Upload Fixes

This document describes the fixes implemented to resolve admin panel loading issues and add file upload functionality.

## Issues Fixed

### 1. Missing `/admin/users` Endpoint
**Problem:** The admin frontend was calling `/admin/users` endpoint which didn't exist in the backend, causing the user management page to continuously load without displaying data.

**Solution:** 
- Added `/admin/users` GET endpoint in `AdminController` for listing users with pagination and search
- Added `/admin/users/{id}` PUT endpoint for updating users
- Added `/admin/users/{id}` DELETE endpoint for deleting users  
- Implemented corresponding service methods in `UserService`:
  - `getAllUsers(username, nickName, pageNum, pageSize)` - with JPA Specification for dynamic filtering
  - `updateUserByAdmin(id, user)` - for admin user updates
  - `deleteUserByAdmin(id)` - for admin user deletion
- Extended `UserRepository` with `JpaSpecificationExecutor` for dynamic queries

### 2. Redis Configuration Issue
**Problem:** The `@ConditionalOnProperty(name = "spring.redis.host", matchIfMissing = false)` configuration could cause issues if Redis is not configured, potentially causing cache-related errors.

**Solution:**
- Updated `RedisConfig` to use `@ConditionalOnProperty(name = "spring.redis.host")` without `matchIfMissing`
- This ensures Redis beans are only created when Redis host is explicitly configured
- The application gracefully handles missing Redis by simply not enabling caching

### 3. File Upload Functionality
**Problem:** The admin frontend resource management page didn't have a way to upload files - users had to manually enter URLs.

**Solution:**
**Frontend (`admin-frontend`):**
- Created `src/api/upload.js` with upload functions:
  - `uploadVideo(file)` - uploads video files to `/file/upload/video`
  - `uploadDocument(file)` - uploads documents to `/file/upload/document`
  - `uploadImage(file)` - uploads images to `/file/upload/image`
  - `uploadCover(file)` - uploads cover images to `/file/upload/cover`
- Updated `src/views/resources/index.vue`:
  - Added `el-upload` component with file selection
  - Added upload progress indicator
  - Added URL copy functionality
  - Modified save flow: upload file first → get URL → save resource with URL
  - Added file type-specific upload tips
  - Form validation to ensure file is uploaded or URL is provided

**Backend:** 
- File upload endpoints already existed in `FileUploadController` and work with the new frontend

## Testing the Changes

### Prerequisites
1. Start MySQL and Redis:
   ```bash
   docker compose up -d mysql redis
   ```

2. Create admin user (username: `admin`, password: `admin123`):
   ```bash
   docker exec -i studyapp-mysql mysql -uroot -proot studyapp < init.sql
   ```

### Testing Backend

1. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

2. Test admin login:
   ```bash
   curl -X POST http://localhost:8080/api/admin/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
   ```

3. Save the token from the response and test the `/admin/users` endpoint:
   ```bash
   TOKEN="<your_jwt_token_here>"
   curl -H "Authorization: Bearer $TOKEN" \
     "http://localhost:8080/api/admin/users?pageNum=1&pageSize=10"
   ```

### Testing Frontend

1. Install dependencies:
   ```bash
   cd admin-frontend
   npm install
   ```

2. Start the development server:
   ```bash
   npm run dev
   ```

3. Open browser to `http://localhost:5173`

4. Login with:
   - Username: `admin`
   - Password: `admin123`

5. Test the following:
   - **User Management Page**: Navigate to Users → should load user list without infinite loading
   - **Resource Management Page**: 
     - Click "新增资源" (Add Resource)
     - Select resource type (Video/Document)
     - Click "选择文件" (Select File) to upload a file
     - Watch the upload progress
     - URL should be automatically filled after upload
     - Save the resource

## API Endpoints Added

### GET `/admin/users`
Get paginated list of users with optional filtering.

**Query Parameters:**
- `username` (optional): Filter by username (partial match)
- `nickName` (optional): Filter by nickname (partial match)  
- `pageNum` (default: 1): Page number
- `pageSize` (default: 10): Items per page

**Response:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 10,
    "number": 0
  }
}
```

### PUT `/admin/users/{id}`
Update a user's information (admin).

**Request Body:**
```json
{
  "nickName": "新昵称",
  "email": "email@example.com",
  "phone": "13800138000",
  "enabled": true
}
```

### DELETE `/admin/users/{id}`
Delete a user (admin).

## Files Modified

### Backend
- `src/main/java/com/studyapp/config/RedisConfig.java` - Fixed conditional property
- `src/main/java/com/studyapp/controller/AdminController.java` - Added user management endpoints
- `src/main/java/com/studyapp/service/UserService.java` - Added admin user management methods
- `src/main/java/com/studyapp/repository/UserRepository.java` - Extended with JpaSpecificationExecutor
- `init.sql` - Added admin user initialization script

### Frontend
- `admin-frontend/src/api/upload.js` - New file upload API module
- `admin-frontend/src/views/resources/index.vue` - Added file upload component and functionality

## Notes

- All endpoints require JWT authentication with ADMIN role
- File uploads use multipart/form-data encoding
- Upload timeouts: Video (5 min), Document (1 min), Image (default)
- The admin user password should be changed in production
- Redis is optional - the application works without it, but caching will be disabled
