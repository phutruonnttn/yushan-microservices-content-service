# Yushan Content Service

> 📚 **Content Service for Yushan Platform (Phase 2 - Microservices)** - Manages novels, chapters, genres, tags, and all content-related operations for the web novel platform.

## 📋 Overview

Content Service is one of the main microservices of Yushan Platform (Phase 2), responsible for managing all novel content, including novels, chapters, categories, and search. This service uses Elasticsearch for advanced search, S3/MinIO for file storage, and Kafka to publish events.

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
git clone https://github.com/phutruonnttn/yushan-microservices-service-registry
cd yushan-microservices-service-registry

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
git clone https://github.com/phutruonnttn/yushan-microservices-content-service.git
cd yushan-microservices-content-service

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
- **POST** `/api/v1/novels` - Create new novel (AUTHOR/ADMIN)
- **GET** `/api/v1/novels/{id}` - Get novel by ID
- **GET** `/api/v1/novels/uuid/{uuid}` - Get novel by UUID
- **PUT** `/api/v1/novels/{id}` - Update novel (AUTHOR/ADMIN)
- **DELETE** `/api/v1/novels/{id}` - Archive novel (soft delete)
- **POST** `/api/v1/novels/{id}/unarchive` - Unarchive novel (ADMIN)
- **POST** `/api/v1/novels/{id}/view` - Increment view count
- **GET** `/api/v1/novels` - List novels (with pagination and filters)
- **GET** `/api/v1/novels/admin/all` - Get all novels including archived (ADMIN)
- **POST** `/api/v1/novels/{id}/submit-review` - Submit novel for review (AUTHOR)
- **POST** `/api/v1/novels/{id}/approve` - Approve novel (ADMIN)
- **POST** `/api/v1/novels/{id}/reject` - Reject novel (ADMIN)
- **POST** `/api/v1/novels/{id}/hide` - Hide novel (ADMIN/AUTHOR)
- **POST** `/api/v1/novels/{id}/unhide` - Unhide novel (ADMIN/AUTHOR)
- **GET** `/api/v1/novels/admin/under-review` - Get novels under review (ADMIN)
- **GET** `/api/v1/novels/author/{authorId}` - Get novels by author
- **GET** `/api/v1/novels/category/{categoryId}` - Get novels by category
- **GET** `/api/v1/novels/count` - Get novel count (with filters)
- **POST** `/api/v1/novels/batch/get` - Batch get novels by IDs
- **GET** `/api/v1/novels/{id}/vote-count` - Get novel vote count
- **POST** `/api/v1/novels/{id}/vote` - Vote for novel
- **PUT** `/api/v1/novels/{id}/rating` - Update novel rating

### Chapter Management
- **POST** `/api/v1/chapters` - Create new chapter (AUTHOR/ADMIN)
- **POST** `/api/v1/chapters/batch` - Batch create chapters (AUTHOR/ADMIN)
- **GET** `/api/v1/chapters/{uuid}` - Get chapter by UUID
- **GET** `/api/v1/chapters/novel/{novelId}/number/{chapterNumber}` - Get chapter by novel ID and number
- **GET** `/api/v1/chapters/novel/{novelId}` - List chapters by novel (with pagination)
- **GET** `/api/v1/chapters/novel/{novelId}/statistics` - Get chapter statistics (AUTHOR/ADMIN)
- **PUT** `/api/v1/chapters` - Update chapter (AUTHOR/ADMIN)
- **PATCH** `/api/v1/chapters/publish` - Publish/unpublish chapter (AUTHOR/ADMIN)
- **PATCH** `/api/v1/chapters/novel/{novelId}/publish` - Batch publish chapters (AUTHOR/ADMIN)
- **POST** `/api/v1/chapters/{uuid}/view` - Increment chapter view count
- **GET** `/api/v1/chapters/search` - Search chapters (with filters)
- **DELETE** `/api/v1/chapters/{uuid}` - Delete chapter (AUTHOR/ADMIN)
- **DELETE** `/api/v1/chapters/novel/{novelId}` - Delete all chapters of a novel (AUTHOR/ADMIN)
- **DELETE** `/api/v1/chapters/admin/{uuid}` - Force delete chapter (ADMIN)
- **DELETE** `/api/v1/chapters/admin/novel/{novelId}` - Force delete all chapters (ADMIN)
- **GET** `/api/v1/chapters/{uuid}/next` - Get next chapter UUID
- **GET** `/api/v1/chapters/{uuid}/previous` - Get previous chapter UUID
- **GET** `/api/v1/chapters/exists` - Check if chapter exists
- **GET** `/api/v1/chapters/novel/{novelId}/next-number` - Get next available chapter number (AUTHOR/ADMIN)
- **POST** `/api/v1/chapters/batch/get` - Batch get chapters by IDs

### Category Management
- **GET** `/api/v1/categories` - Get all categories
- **GET** `/api/v1/categories/active` - Get active categories only
- **GET** `/api/v1/categories/{id}` - Get category by ID
- **GET** `/api/v1/categories/slug/{slug}` - Get category by slug
- **GET** `/api/v1/categories/{id}/statistics` - Get category statistics
- **POST** `/api/v1/categories` - Create category (ADMIN)
- **PUT** `/api/v1/categories/{id}` - Update category (ADMIN)
- **DELETE** `/api/v1/categories/{id}` - Soft delete category (ADMIN)
- **DELETE** `/api/v1/categories/{id}/hard` - Hard delete category (ADMIN)

### Search
- **GET** `/api/v1/search` - Combined search (novels and chapters)
- **GET** `/api/v1/search/novels` - Search novels only
- **GET** `/api/v1/search/chapters` - Search chapters only
- **GET** `/api/v1/search/suggestions` - Get search suggestions/autocomplete

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
- **Category** - Category classifications (replaces Genre)
- **NovelCategory** - Novel-category mappings

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

## 📄 License

This project is part of the Yushan Platform ecosystem.

## 🔗 Links

- **API Gateway**: [yushan-microservices-api-gateway](https://github.com/phutruonnttn/yushan-microservices-api-gateway)
- **Service Registry**: [yushan-microservices-service-registry](https://github.com/phutruonnttn/yushan-microservices-service-registry)
- **Config Server**: [yushan-microservices-config-server](https://github.com/phutruonnttn/yushan-microservices-config-server)
- **Platform Documentation**: [yushan-platform-docs](https://github.com/phutruonnttn/yushan-platform-docs) - Complete documentation for all phases
- **Phase 2 Architecture**: See [Phase 2 Microservices Architecture](https://github.com/phutruonnttn/yushan-platform-docs/blob/main/docs/phase2-microservices/PHASE2_MICROSERVICES_ARCHITECTURE.md)
