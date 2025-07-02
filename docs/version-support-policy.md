# 📜 Politique de Support des Versions

<div align="center">

**Comprendre le cycle de vie et le support des versions de GraphQL AutoGen.**

</div>

---

## 🎯 Vue d'ensemble

Ce document décrit la politique de support des versions pour GraphQL AutoGen. Notre objectif est de fournir un support clair et prévisible pour les utilisateurs, tout en permettant une évolution rapide du projet.

---

## 🚀 Stratégie de Versionnement

GraphQL AutoGen suit le [Versionnement Sémantique 2.0.0](https://semver.org/lang/fr/). Cela signifie que les numéros de version `MAJEUR.MINEUR.PATCH` ont une signification spécifique :

-   **`MAJEUR` (X.0.0) :** Changements incompatibles avec les versions précédentes de l'API.
-   **`MINEUR` (0.X.0) :** Ajout de fonctionnalités de manière rétrocompatible.
-   **`PATCH` (0.0.X) :** Corrections de bugs rétrocompatibles.

---

## 🗓️ Cycle de Vie des Versions

### Versions Majeures

-   Les versions majeures introduisent des changements significatifs et peuvent nécessiter des modifications de code de la part des utilisateurs.
-   Elles sont supportées pendant une période définie (par exemple, 12 mois) après leur publication, incluant les corrections de bugs critiques et les mises à jour de sécurité.

### Versions Mineures

-   Les versions mineures ajoutent de nouvelles fonctionnalités et améliorations sans casser la compatibilité ascendante.
-   Elles sont supportées jusqu'à la publication de la prochaine version majeure, avec des corrections de bugs et des mises à jour de sécurité.

### Versions Patch

-   Les versions patch sont publiées fréquemment pour corriger les bugs et les problèmes de sécurité.
-   Elles sont rétrocompatibles et ne devraient pas nécessiter de modifications de code.

---

## 🔒 Mises à Jour de Sécurité

Nous nous engageons à fournir des mises à jour de sécurité rapides pour toutes les versions supportées. Les vulnérabilités de sécurité seront traitées avec la plus haute priorité.

---

## 🔄 Politique de Rétrocompatibilité

-   **Versions Majeures :** Les versions majeures peuvent introduire des changements cassants. Nous nous efforcerons de fournir des guides de migration détaillés.
-   **Versions Mineures et Patch :** Ces versions sont garanties rétrocompatibles. Les utilisateurs devraient pouvoir mettre à jour sans modifier leur code.

---

## 💡 Recommandations

-   **Restez à jour :** Nous recommandons aux utilisateurs de mettre à jour régulièrement vers les dernières versions patch et mineures pour bénéficier des dernières fonctionnalités et corrections.
-   **Planifiez les mises à niveau majeures :** Pour les versions majeures, planifiez une période de mise à niveau pour tester et adapter votre code si nécessaire.

---

**🎉 Nous nous engageons à maintenir GraphQL AutoGen stable et sécurisé !**
