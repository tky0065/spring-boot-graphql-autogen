# 🗺️ Roadmap Publique

<div align="center">

**La feuille de route de GraphQL AutoGen : ce qui est à venir !**

</div>

---

## 🎯 Vue d'ensemble

Cette roadmap présente les fonctionnalités et améliorations prévues pour les futures versions de GraphQL AutoGen. Elle est sujette à changement en fonction des priorités, des retours de la communauté et des évolutions technologiques.

---

## 🚀 Prochaines Versions

### Version 1.1.0 (Prochaine Version Mineure)

-   **Améliorations des Queries :**
    -   Support des paramètres de pagination avancés (filtres complexes, tri).
    -   Support des queries imbriquées avec des résolveurs personnalisés.
-   **Améliorations des Mutations :**
    -   Génération automatique des types de réponse (Payload) pour les mutations.
    -   Gestion des erreurs de validation dans les payloads de mutation.
-   **Intégration Spring Boot DevTools :** Amélioration du rechargement à chaud du schéma.
-   **Support des JARs externes :** Amélioration du scanning d'annotations dans les dépendances JAR.

### Version 1.2.0 (Futures Améliorations)

-   **Sécurité :**
    -   Intégration plus poussée avec Spring Security (`@PreAuthorize`).
    -   Autorisation au niveau des champs GraphQL.
    -   Limitation de la profondeur et de la complexité des requêtes.
-   **Validation et Erreurs :**
    -   Intégration complète avec Bean Validation pour la génération de directives `@constraint`.
    -   Génération automatique des types d'erreur GraphQL.
    -   Gestion centralisée des exceptions GraphQL.
-   **Intégration Spring Data :** Support amélioré pour la génération de requêtes et mutations à partir des interfaces Spring Data JPA/MongoDB/etc.

### Version 2.0.0 (Majeure - Rupture de Compatibilité Potentielle)

-   **Refonte de l'API :** Simplification et modernisation de certaines interfaces et annotations.
-   **Performance :** Optimisations majeures pour la génération de schémas très volumineux.
-   **Support de Spécifications Avancées :** Implémentation de fonctionnalités GraphQL plus avancées (ex: Directives personnalisées, Federation).

---

## 💡 Comment Contribuer à la Roadmap

Vos retours sont essentiels ! N'hésitez pas à :

-   **Ouvrir des issues GitHub :** Pour signaler des bugs, proposer des fonctionnalités ou des améliorations.
-   **Participer aux discussions :** Rejoignez notre communauté sur Discord/Slack pour discuter des priorités et des implémentations.
-   **Soumettre des Pull Requests :** Si vous souhaitez implémenter une fonctionnalité de la roadmap, contactez-nous d'abord pour coordonner les efforts.

---

**🎉 Ensemble, façonnons l'avenir de GraphQL AutoGen !**
