# Yushan Content Service

> 📚 **Content Service for Yushan Webnovel Platform.** - Manages novels, chapters, genres, tags, and all content-related operations for the web novel platform.

# Yushan Platform - Content Service Setup Guide

## Architecture Overview

```
┌─────────────────────────────┐
│   Eureka Service Registry   │
│       localhost:8761        │
└──────────────┬──────────────┘
               │
┌──────────────┴──────────────┐
│   Service Registration &     │
│      Discovery Layer         │
└──────────────┬──────────────┘
               │
    ┌──────────┴──────────┬───────────────┬──────────┬──────────┐
    │                     │               │          │          │
    ▼                     ▼               ▼          ▼          ▼
┌────────┐          ┌─────────┐  ┌────────────┐ ┌──────────┐ ┌──────────┐
│  User  │          │ Content │  │ Engagement │ │Gamifica- │ │Analytics │
│Service │◄────────►│ Service │─►│  Service   │ │  tion    │ │ Service  │
│ :8081  │          │  :8082  │  │   :8084    │ │ Service  │ │  :8083   │
└────────┘          └────┬────┘  └────────────┘ │  :8085   │ └──────────┘
                         │              │        └──────────┘      │
                         └──────────────┴──────────────────────────┘
                    Inter-service Communication
                      (via Feign Clients)
                         │
            ┌────────────┴────────────┐
            │  Content Management     │
            │  Novels, Chapters       │
            │  Search & Discovery     │
            └─────────────────────────┘
                         │
            ┌────────────┴────────────┐
            │   File Storage          │
            │   (S3/MinIO)            │
            │   Cover Images, Assets  │
            └─────────────────────────┘
```

---
## Prerequisites

Before setting up the Content Service, ensure you have:
1. **Java 21** installed
2. **Maven 3.8+** or use the included Maven wrapper
3. **Eureka Service Registry** running
4. **PostgreSQL 15+** (for content data storage)
5. **Redis** (for caching popular content)
6. **Elasticsearch** (for advanced content search)
7. **S3/MinIO** (for storing cover images and assets)

---
## Step 1: Start Eureka Service Registry

**IMPORTANT**: The Eureka Service Registry must be running before starting any microservice.

```bash
# Clone the service registry repository
git clone https://github.com/maugus0/yushan-platform-service-registry
cd yushan-platform-service-registry

# Option 1: Run with Docker (Recommended)
docker-compose up -d

# Option 2: Run locally
./mvnw spring-boot:run
```

### Verify Eureka is Running

- Open: http://localhost:8761
- You should see the Eureka dashboard

---

## Step 2: Clone the Content Service Repository

```bash
git clone https://github.com/maugus0/yushan-content-service.git
cd yushan-content-service

# Option 1: Run with Docker (Recommended)
docker-compose up -d

# Option 2: Run locally (requires PostgreSQL 15, Redis, and Elasticsearch running beforehand)
./mvnw spring-boot:run
```

---

## Expected Output

### Console Logs (Success)

```
2024-10-16 10:30:15 - Starting ContentServiceApplication
2024-10-16 10:30:18 - Tomcat started on port(s): 8082 (http)
2024-10-16 10:30:20 - DiscoveryClient_CONTENT-SERVICE/content-service:8082 - registration status: 204
2024-10-16 10:30:20 - Started ContentServiceApplication in 9.3 seconds
```

### Eureka Dashboard

```
Instances currently registered with Eureka:
✅ CONTENT-SERVICE - 1 instance(s)
   Instance ID: content-service:8082
   Status: UP (1)
```

---

## API Endpoints

### Health Check
- **GET** `/api/v1/health` - Service health status

### Novel Management
- **POST** `/api/v1/novels` - Create new novel
- **GET** `/api/v1/novels/{novelId}` - Get novel details
- **PUT** `/api/v1/novels/{novelId}` - Update novel
- **DELETE** `/api/v1/novels/{novelId}` - Delete novel
- **PUT** `/api/v1/novels/{novelId}/publish` - Publish novel
- **PUT** `/api/v1/novels/{novelId}/unpublish` - Unpublish novel
- **PUT** `/api/v1/novels/{novelId}/cover` - Upload cover image
- **GET** `/api/v1/novels/{novelId}/statistics` - Get novel statistics

### Chapter Management
- **POST** `/api/v1/novels/{novelId}/chapters` - Create new chapter
- **GET** `/api/v1/chapters/{chapterId}` - Get chapter details
- **PUT** `/api/v1/chapters/{chapterId}` - Update chapter
- **DELETE** `/api/v1/chapters/{chapterId}` - Delete chapter
- **PUT** `/api/v1/chapters/{chapterId}/publish` - Publish chapter
- **GET** `/api/v1/novels/{novelId}/chapters` - List novel chapters
- **PUT** `/api/v1/chapters/{chapterId}/reorder` - Reorder chapters

### Content Discovery
- **GET** `/api/v1/novels/search` - Search novels
- **GET** `/api/v1/novels/trending` - Get trending novels
- **GET** `/api/v1/novels/new-releases` - Get new releases
- **GET** `/api/v1/novels/recommended` - Get recommended novels
- **GET** `/api/v1/novels/popular` - Get popular novels
- **GET** `/api/v1/novels/genre/{genreId}` - Get novels by genre
- **GET** `/api/v1/novels/author/{authorId}` - Get novels by author

### Genre Management
- **GET** `/api/v1/genres` - List all genres
- **GET** `/api/v1/genres/{genreId}` - Get genre details
- **POST** `/api/v1/genres` - Create genre (admin)
- **PUT** `/api/v1/genres/{genreId}` - Update genre (admin)
- **DELETE** `/api/v1/genres/{genreId}` - Delete genre (admin)

### Tag Management
- **GET** `/api/v1/tags` - List all tags
- **GET** `/api/v1/tags/{tagId}` - Get tag details
- **POST** `/api/v1/tags` - Create tag
- **GET** `/api/v1/tags/search` - Search tags
- **GET** `/api/v1/tags/popular` - Get popular tags

### Author Operations
- **GET** `/api/v1/authors/{authorId}` - Get author profile
- **GET** `/api/v1/authors/{authorId}/novels` - Get author's novels
- **GET** `/api/v1/authors/{authorId}/statistics` - Get author statistics
- **GET** `/api/v1/authors/search` - Search authors

### Collection Management
- **GET** `/api/v1/collections` - List collections
- **GET** `/api/v1/collections/{collectionId}` - Get collection details
- **POST** `/api/v1/collections` - Create collection (admin)
- **PUT** `/api/v1/collections/{collectionId}` - Update collection
- **POST** `/api/v1/collections/{collectionId}/novels/{novelId}` - Add novel to collection

### Content Moderation
- **GET** `/api/v1/moderation/pending` - Get pending content (admin)
- **PUT** `/api/v1/moderation/novels/{novelId}/approve` - Approve novel
- **PUT** `/api/v1/moderation/novels/{novelId}/reject` - Reject novel
- **PUT** `/api/v1/moderation/chapters/{chapterId}/flag` - Flag chapter

---

## Key Features

### 📖 Novel Management
- Create and edit novels
- Cover image upload
- Novel metadata (title, description, synopsis)
- Multiple genres and tags
- Publication status management
- Draft saving
- Version control

### 📝 Chapter Management
- Rich text chapter content
- Chapter ordering and numbering
- Scheduled publishing
- Chapter locking (premium content)
- Draft chapters
- Chapter notes and author comments

### 🔍 Advanced Search
- Full-text search with Elasticsearch
- Filter by genre, tags, status
- Sort by popularity, date, rating
- Search autocomplete
- Similar novel recommendations
- Advanced filtering options

### 🎨 Content Discovery
- Trending novels algorithm
- New releases
- Editor's picks
- Genre-based browsing
- Tag-based navigation
- Personalized recommendations

### 🏷️ Categorization
- Multi-genre support
- Flexible tagging system
- Custom taxonomies
- Genre hierarchies
- Tag popularity tracking

### 👨‍💼 Author Management
- Author profiles
- Author statistics
- Multiple pen names
- Author verification
- Author dashboard
- Revenue tracking

### 📚 Collections & Curation
- Featured collections
- Themed collections
- Editorial collections
- Auto-generated collections
- Collection management

### 🛡️ Content Moderation
- Content approval workflow
- Automated content filtering
- Copyright detection
- Plagiarism checking
- Content reporting system
- Moderation queue

### 📊 Content Analytics
- View counts
- Read time tracking
- Chapter completion rates
- Popular chapters
- Reader demographics

---

## Database Schema

The Content Service uses the following key entities:

- **Novel** - Core novel information
- **Chapter** - Chapter content and metadata
- **Genre** - Genre categories
- **Tag** - Content tags
- **Author** - Author profiles
- **NovelGenre** - Novel-genre mappings
- **NovelTag** - Novel-tag mappings
- **Collection** - Curated collections
- **ContentModeration** - Moderation records
- **NovelStatistics** - Aggregated novel statistics

---

## Next Steps

Once this basic setup is working:
1. ✅ Create database entities (Novel, Chapter, Genre, Tag, etc.)
2. ✅ Set up Flyway migrations
3. ✅ Create repositories and services
4. ✅ Implement API endpoints
5. ✅ Set up Elasticsearch indexing
6. ✅ Add Feign clients for inter-service communication
7. ✅ Implement file storage with S3/MinIO
8. ✅ Set up Redis caching for popular content
9. ✅ Add content moderation workflow
10. ✅ Implement search and recommendation algorithms

---

## Troubleshooting

**Problem: Service won't register with Eureka**
- Ensure Eureka is running: `docker ps`
- Check logs: Look for "DiscoveryClient" messages
- Verify defaultZone URL is correct

**Problem: Port 8082 already in use**
- Find process: `lsof -i :8082` (Mac/Linux) or `netstat -ano | findstr :8082` (Windows)
- Kill process or change port in application.yml

**Problem: Database connection fails**
- Verify PostgreSQL is running: `docker ps | grep yushan-postgres`
- Check database credentials in application.yml
- Test connection: `psql -h localhost -U yushan_content -d yushan_content`

**Problem: Elasticsearch connection fails**
- Verify Elasticsearch is running: `curl http://localhost:9200`
- Check Elasticsearch URI in application.yml
- Review Elasticsearch logs

**Problem: File upload fails**
- Check S3/MinIO credentials
- Verify bucket exists and is accessible
- Review storage configuration
- Check file size limits

**Problem: Search not working**
- Ensure Elasticsearch indices are created
- Re-index content: Run reindexing job
- Check Elasticsearch mappings
- Review search query logs

**Problem: Build fails**
- Ensure Java 21 is installed: `java -version`
- Check Maven: `./mvnw -version`
- Clean and rebuild: `./mvnw clean install -U`

---

## Performance Tips
1. **Content Caching**: Cache popular novels and chapters in Redis
2. **Search Optimization**: Use Elasticsearch for all search operations
3. **CDN**: Serve cover images and static assets via CDN
4. **Database Indexing**: Index frequently queried fields
5. **Lazy Loading**: Load chapter content on-demand
6. **Pagination**: Always paginate large result sets
7. **Read Replicas**: Use database read replicas for queries

---

## Content Guidelines
Enforce platform content policies:
- Age-appropriate content ratings
- Copyright compliance
- Plagiarism prevention
- Content quality standards
- Prohibited content detection
- Community guidelines enforcement

---

## Search Features
Elasticsearch-powered search capabilities:
- Full-text search across novels and chapters
- Fuzzy matching for typos
- Faceted search (genre, tags, status)
- Search suggestions and autocomplete
- Relevance scoring
- Search analytics

---

## Inter-Service Communication
The Content Service communicates with:
- **User Service**: Verify author identity and permissions
- **Engagement Service**: Fetch ratings and reviews
- **Analytics Service**: Send content performance metrics
- **Gamification Service**: Track content milestones

---

## Security Considerations
- Validate author ownership before updates/deletes
- Implement content access control (public/private/premium)
- Sanitize HTML content to prevent XSS
- Rate limit content creation endpoints
- Validate file uploads (size, type)
- Implement copyright protection
- Monitor for content policy violations

---

## File Storage
Content Service handles various file types:
- **Cover Images**: JPEG, PNG (max 5MB)
- **Author Photos**: JPEG, PNG (max 2MB)
- **Chapter Attachments**: Limited file types
- **Backup**: Automatic S3 versioning

---

## Monitoring
The Content Service exposes metrics through:
- Spring Boot Actuator endpoints (`/actuator/metrics`)
- Custom content metrics (novels created, chapters published)
- Search query performance
- Storage usage statistics
- Cache hit rates

---

## License
This project is part of the Yushan Platform ecosystem.
