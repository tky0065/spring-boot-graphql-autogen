# Release Notes

This document contains the release notes for all versions of Spring Boot GraphQL Auto-Generator.

## Version 1.0.0 - 2025-07-01

### New Features
- Initial release of Spring Boot GraphQL Auto-Generator.
- Automatic GraphQL schema generation from Java annotations.
- Support for GraphQL Types, Inputs, Enums, Scalars, Interfaces, and Unions.
- Generation of GraphQL Queries, Mutations, and Subscriptions from Spring controllers.
- DataLoader integration for N+1 query optimization.
- Pagination support (Relay, Offset, Page-based).
- Spring Boot auto-configuration for easy setup.
- Maven and Gradle plugins for build-time schema generation.
- Standalone CLI tool for schema generation and validation.
- Comprehensive documentation (Quick Start, Annotations Reference, Migration Guide, Plugin Docs, FAQ).
- Example applications (E-commerce, Blog, Library).
- CI/CD pipeline with GitHub Actions for automated testing and release.

### Bug Fixes
- Fixed compilation error in `graphql-autogen-core` related to `GraphQLType`.
- Fixed auto-configuration failure in `graphql-autogen-spring-boot-starter` due to incorrect package names in `spring.factories` and `spring-configuration-metadata.json`.

### Improvements
- Improved documentation for quick start, annotations, and migration.
- Added detailed troubleshooting guide for type conflicts.

