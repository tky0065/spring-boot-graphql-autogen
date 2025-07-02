# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Removed

### Fixed

### Security

## [1.0.0] - 2025-07-01

### Added
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

### Fixed
- Compilation error in `graphql-autogen-core` related to `GraphQLType`.
- Auto-configuration failure in `graphql-autogen-spring-boot-starter` due to incorrect package names in `spring.factories` and `spring-configuration-metadata.json`.
