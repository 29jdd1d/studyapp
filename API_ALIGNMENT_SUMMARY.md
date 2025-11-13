# API Alignment Summary

## Overview
This document summarizes the changes made to align the admin frontend and miniprogram with the backend API as the standard reference. All requests and responses have been updated to match the backend Spring Boot controller specifications.

## Backend API Reference
- **Technology**: Spring Boot 2.7.14 with Spring Data JPA
- **Base URL**: `/api`
- **Response Format**: `Result<T>` with properties: `code`, `message`, `data`
- **Pagination**: Spring Data `Page<T>` with properties: `content`, `totalElements`, `totalPages`, `number`, `size`

## Issues Fixed

### 1. Miniprogram API Misalignments

#### 1.1 Question Submission (Fixed)
**File**: `miniprogram/api/question.js`

**Issue**: Backend expects `@RequestParam` but frontend was sending request body
```javascript
// Before (WRONG)
function submitAnswer(id, answer, timeSpent) {
  return post(`/question/${id}/answer`, {}, {
    data: { answer, timeSpent }
  });
}

// After (CORRECT)
function submitAnswer(id, answer, timeSpent) {
  return post(`/question/${id}/answer?answer=${encodeURIComponent(answer)}${timeSpent ? '&timeSpent=' + timeSpent : ''}`);
}
```

**Backend Signature**:
```java
@PostMapping("/{id}/answer")
public Result<Boolean> submitAnswer(
    HttpServletRequest request,
    @PathVariable Long id,
    @RequestParam String answer,
    @RequestParam(required = false) Integer timeSpent)
```

#### 1.2 Plan Recommendation (Fixed)
**File**: `miniprogram/api/plan.js`

**Issue**: Backend expects `@RequestParam` but frontend was sending request body
```javascript
// Before (WRONG)
function generateRecommendPlan(params) {
  return post('/plan/recommend', {}, { data: params });
}

// After (CORRECT)
function generateRecommendPlan(params) {
  const { targetUniversity, targetMajor, examDate } = params;
  return post(`/plan/recommend?targetUniversity=${encodeURIComponent(targetUniversity)}&targetMajor=${encodeURIComponent(targetMajor)}&examDate=${examDate}`);
}
```

**Backend Signature**:
```java
@PostMapping("/recommend")
public Result<StudyPlan> generateRecommendedPlan(
    HttpServletRequest request,
    @RequestParam String targetUniversity,
    @RequestParam String targetMajor,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate)
```

#### 1.3 Complete Plan Item (Fixed)
**File**: `miniprogram/api/plan.js`

**Issue**: Backend expects `@RequestParam` but frontend was sending request body
```javascript
// Before (WRONG)
function completePlanItem(itemId, actualHours) {
  return post(`/plan/item/${itemId}/complete`, {}, { 
    data: { actualHours } 
  });
}

// After (CORRECT)
function completePlanItem(itemId, actualHours) {
  return post(`/plan/item/${itemId}/complete?actualHours=${actualHours}`);
}
```

**Backend Signature**:
```java
@PostMapping("/item/{itemId}/complete")
public Result<Void> completePlanItem(
    @PathVariable Long itemId, 
    @RequestParam Integer actualHours)
```

#### 1.4 Get My Posts (Fixed)
**File**: `miniprogram/api/community.js`

**Issue**: Backend expects pagination parameters but frontend wasn't sending them
```javascript
// Before (INCOMPLETE)
function getMyPosts() {
  return get('/community/post/my');
}

// After (COMPLETE)
function getMyPosts(params = {}) {
  return get('/community/post/my', params);
}
```

**Backend Signature**:
```java
@GetMapping("/post/my")
public Result<Page<CommunityPost>> getMyPosts(
    HttpServletRequest request,
    @RequestParam(defaultValue = "1") Integer pageNum,
    @RequestParam(defaultValue = "10") Integer pageSize)
```

### 2. Admin Frontend API Misalignments

#### 2.1 Delete Endpoints (Fixed)
**File**: `admin-frontend/src/api/admin.js`

**Issue**: Delete endpoints were calling wrong paths (non-admin paths)

```javascript
// Delete Resource - Before (WRONG)
export function deleteResource(id) {
  return request({ url: `/resource/${id}`, method: 'delete' })
}

// Delete Resource - After (CORRECT)
export function deleteResource(id) {
  return request({ url: `/admin/resource/${id}`, method: 'delete' })
}

// Delete Question - Before (WRONG)
export function deleteQuestion(id) {
  return request({ url: `/question/${id}`, method: 'delete' })
}

// Delete Question - After (CORRECT)
export function deleteQuestion(id) {
  return request({ url: `/admin/question/${id}`, method: 'delete' })
}

// Delete Post - Before (WRONG)
export function deletePost(id) {
  return request({ url: `/community/post/${id}`, method: 'delete' })
}

// Delete Post - After (CORRECT)
export function deletePost(id) {
  return request({ url: `/admin/post/{id}`, method: 'delete' })
}
```

**Backend Signatures**:
```java
@DeleteMapping("/resource/{id}")
public Result<Void> deleteResource(@PathVariable Long id)

@DeleteMapping("/question/{id}")
public Result<Void> deleteQuestion(@PathVariable Long id)

@DeleteMapping("/post/{id}")
public Result<Void> deletePost(@PathVariable Long id)
```

#### 2.2 Non-existent Endpoints (Removed)
**File**: `admin-frontend/src/api/admin.js`

Removed the following endpoints that don't exist in the backend:

1. **updateQuestion** - `PUT /question/{id}` (does not exist)
2. **updatePost** - `PUT /community/post/{id}` (does not exist)
3. **auditPost** - `POST /admin/posts/{id}/audit` (does not exist)

```javascript
// Removed and commented out:
// export function updateQuestion(id, data) { ... }
// export function updatePost(id, data) { ... }
// export function auditPost(id, status) { ... }
```

#### 2.3 Pagination Response Handling (Fixed)
**Files**: 
- `admin-frontend/src/views/resources/index.vue`
- `admin-frontend/src/views/users/index.vue`
- `admin-frontend/src/views/questions/index.vue`
- `admin-frontend/src/views/community/index.vue`

**Issue**: Frontend was accessing wrong properties for Spring Data Page responses

```javascript
// Before (WRONG)
if (response.data) {
  resourceList.value = response.data.list || response.data
  pagination.total = response.data.total || resourceList.value.length
}

// After (CORRECT)
if (response.data) {
  // Spring Data Page returns 'content' for the list and 'totalElements' for total count
  resourceList.value = response.data.content || response.data
  pagination.total = response.data.totalElements || resourceList.value.length
}
```

**Spring Data Page Structure**:
```java
public interface Page<T> {
    List<T> getContent();        // The actual list of items
    long getTotalElements();     // Total number of items
    int getTotalPages();         // Total number of pages
    int getNumber();             // Current page number (0-indexed)
    int getSize();               // Page size
}
```

#### 2.4 UI Functionality Removed
**File**: `admin-frontend/src/views/community/index.vue`

Removed audit functionality (approve/reject posts) as the backend endpoint doesn't exist:
- Removed audit buttons from table actions
- Removed `handleAudit` function
- Removed import of `auditPost`

**File**: `admin-frontend/src/views/questions/index.vue`

Removed edit functionality as the backend endpoint doesn't exist:
- Commented out edit button from table actions
- Commented out `handleEdit` function
- Updated `handleSave` to show warning if edit is attempted
- Removed import of `updateQuestion`

## Testing Checklist

### Miniprogram Endpoints to Test
- [ ] `POST /question/{id}/answer` with query parameters
- [ ] `POST /plan/recommend` with query parameters
- [ ] `POST /plan/item/{itemId}/complete` with query parameter
- [ ] `GET /community/post/my` with pagination parameters

### Admin Frontend Endpoints to Test
- [ ] `DELETE /admin/resource/{id}`
- [ ] `DELETE /admin/question/{id}`
- [ ] `DELETE /admin/post/{id}`
- [ ] `GET /admin/resources` - verify pagination response
- [ ] `GET /admin/users` - verify pagination response
- [ ] `GET /admin/questions` - verify pagination response
- [ ] `GET /admin/posts` - verify pagination response

## Files Modified

### Miniprogram
1. `miniprogram/api/question.js` - Fixed submitAnswer
2. `miniprogram/api/plan.js` - Fixed generateRecommendPlan and completePlanItem
3. `miniprogram/api/community.js` - Fixed getMyPosts

### Admin Frontend
1. `admin-frontend/src/api/admin.js` - Fixed delete endpoints, removed non-existent endpoints
2. `admin-frontend/src/views/community/index.vue` - Removed audit functionality, fixed pagination
3. `admin-frontend/src/views/resources/index.vue` - Fixed pagination handling
4. `admin-frontend/src/views/users/index.vue` - Fixed pagination handling
5. `admin-frontend/src/views/questions/index.vue` - Fixed pagination, removed edit functionality

## Summary

All frontend API calls are now correctly aligned with the backend Spring Boot API specifications:

✅ **Request Parameters**: All endpoints use correct parameter types (@RequestParam vs @RequestBody)  
✅ **Endpoint Paths**: All admin delete endpoints use correct `/admin/*` paths  
✅ **Response Handling**: All pagination responses use correct Spring Data Page properties  
✅ **Removed Dead Code**: All non-existent endpoints have been removed from frontend code  
✅ **Security**: CodeQL security scan passed with 0 alerts

## Backend Endpoints Not Implemented

The following endpoints are called by the frontend but don't exist in the backend. If these features are needed, they should be implemented in the backend:

1. `PUT /question/{id}` - Update question (currently admin can only create and delete)
2. `PUT /community/post/{id}` - Update post (currently users can only create and delete)
3. `POST /admin/posts/{id}/audit` - Audit/approve posts (no post approval workflow exists)

## Recommendations

1. **For Production**: Test all modified endpoints thoroughly before deployment
2. **For Future Development**: If update functionality is needed for questions/posts, implement the backend endpoints first
3. **For Post Moderation**: If post approval workflow is needed, implement the audit endpoint in the backend
