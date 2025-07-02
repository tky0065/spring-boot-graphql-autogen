# 📝 Documentation de Release

<div align="center">

**Guide complet pour la préparation et la publication d'une nouvelle version de GraphQL AutoGen.**

</div>

---

## 🎯 Vue d'ensemble

Ce document détaille le processus de release pour le projet GraphQL AutoGen, incluant les étapes de préparation, de publication sur Maven Central, et de communication autour de la nouvelle version.

---

## 🚀 Processus de Release

### 1. Préparation de la Release

-   **Mettre à jour le `pom.xml` :** Mettre à jour la version du projet dans le `pom.xml` principal et dans les sous-modules.
-   **Mettre à jour le `CHANGELOG.md` :** Ajouter toutes les nouvelles fonctionnalités, corrections de bugs et changements majeurs depuis la dernière version.
-   **Mettre à jour la documentation :** S'assurer que toute la documentation (guides, références) est à jour avec les nouvelles fonctionnalités.
-   **Exécuter les tests :** Lancer la suite complète des tests unitaires et d'intégration pour s'assurer de la stabilité.

### 2. Publication sur Maven Central

La publication est automatisée via GitHub Actions. Le workflow `release.yml` gère les étapes suivantes :

-   **Build du projet :** Compilation et packaging des artifacts.
-   **Signature GPG :** Signature de tous les artifacts avec une clé GPG.
-   **Déploiement sur Sonatype OSSRH :** Téléchargement des artifacts signés sur le dépôt de staging de Sonatype.
-   **Release sur Maven Central :** Une fois le staging validé, les artifacts sont synchronisés avec Maven Central.

### 3. Communication de la Release

-   **GitHub Release :** Créer une nouvelle release sur GitHub avec les notes de version complètes.
-   **Annonce :** Publier une annonce sur les canaux de communication (blog, réseaux sociaux, etc.).

---

## 📋 Checklist de Release

- [ ] Mettre à jour la version dans `pom.xml`.
- [ ] Mettre à jour `CHANGELOG.md`.
- [ ] Vérifier et mettre à jour la documentation.
- [ ] Exécuter `mvn clean install` localement.
- [ ] Exécuter `mvn deploy -P release` pour le staging sur Sonatype.
- [ ] Vérifier le staging sur Sonatype OSSRH.
- [ ] Créer une nouvelle release sur GitHub.
- [ ] Annoncer la nouvelle version.

---

**🎉 Prêt à publier votre prochaine version !**
