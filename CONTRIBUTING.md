# 🤝 Guide de Contribution - Spring Boot GraphQL Auto-Generator

Merci de votre intérêt pour contribuer à Spring Boot GraphQL Auto-Generator ! Ce guide vous aidera à contribuer efficacement au projet.

## 🎯 Comment contribuer

### Vous pouvez contribuer de plusieurs façons :

1. **🐛 Signaler des bugs** - Aidez-nous à identifier et corriger les problèmes
2. **💡 Proposer des fonctionnalités** - Partagez vos idées d'amélioration
3. **🔧 Corriger des bugs** - Soumettez des corrections de code
4. **✨ Ajouter des fonctionnalités** - Implémentez de nouvelles capacités
5. **📖 Améliorer la documentation** - Aidez les autres utilisateurs
6. **🧪 Ajouter des tests** - Renforcez la robustesse du projet

## 🛠️ Configuration de l'environnement

### Prérequis

```bash
# Versions requises
Java: 21+
Maven: 3.8+
Git: 2.30+
IDE: IntelliJ IDEA 2023+ (recommandé)
```

### 🔧 Setup initial

```bash
# Clone votre fork
git clone https://github.com/VOTRE_USERNAME/spring-boot-graphql-autogen.git
cd spring-boot-graphql-autogen

# Build complet avec tests
./mvnw clean install

# Tous les tests
./mvnw test
```

## 📝 Standards de code

### ☕ Conventions Java

```java
// ✅ Bon exemple
@Component
@Slf4j
public class DefaultSchemaGenerator implements SchemaGenerator {
    
    private final TypeResolver typeResolver;
    
    public DefaultSchemaGenerator(TypeResolver typeResolver) {
        this.typeResolver = Objects.requireNonNull(typeResolver, "TypeResolver cannot be null");
    }
    
    @Override
    public GraphQLSchema generateSchema(List<Class<?>> annotatedClasses) {
        log.debug("Generating GraphQL schema for {} classes", annotatedClasses.size());
        
        try {
            return buildSchemaFromClasses(annotatedClasses);
        } catch (Exception e) {
            throw new SchemaGenerationException("Failed to generate schema", e);
        }
    }
}
```

### 🏷️ Convention des commits

Nous utilisons la convention [Conventional Commits](https://www.conventionalcommits.org/) :

```bash
# Format
<type>(<scope>): <description>

# Types
feat:     Nouvelle fonctionnalité
fix:      Correction de bug
docs:     Documentation uniquement
test:     Ajout/modification de tests
chore:    Maintenance (build, CI, etc.)

# Exemples
feat(core): add support for GraphQL unions
fix(starter): resolve auto-configuration issue
docs(readme): update quick start guide
test(integration): add tests for pagination
```

## 🔄 Processus de Pull Request

### 📋 Checklist avant soumission

- [ ] **Code** compilé sans erreurs
- [ ] **Tests** tous passants (`./mvnw test`)
- [ ] **Documentation** mise à jour
- [ ] **Commit messages** suivent la convention
- [ ] **Branch** à jour avec main

### 🚀 Workflow

1. **Fork** le repository
2. **Créer** une branche feature (`git checkout -b feat/ma-fonctionnalite`)
3. **Commit** vos changements (`git commit -m 'feat: ajouter nouvelle fonctionnalité'`)
4. **Push** vers la branche (`git push origin feat/ma-fonctionnalite`)
5. **Créer** une Pull Request

## 🐛 Signaler des bugs

### 📝 Template d'issue

```markdown
## 🐛 Description du bug
Description claire et concise du bug.

## 🔄 Étapes pour reproduire
1. Étape 1
2. Étape 2
3. Étape 3

## ✅ Comportement attendu
Description de ce qui devrait se passer.

## ❌ Comportement actuel
Description de ce qui se passe actuellement.

## 📱 Environnement
- **OS**: [e.g. Windows 10, macOS 12.0]
- **Java**: [e.g. OpenJDK 21]
- **Spring Boot**: [e.g. 3.3.1]
- **Version GraphQL AutoGen**: [e.g. 1.0.1]
```

## 👥 Code de conduite

### 🤝 Nos engagements

En tant que contributeurs et mainteneurs de ce projet, nous nous engageons à :

- **Respecter** tous les participants
- **Accueillir** les nouveaux contributeurs
- **Être constructifs** dans nos feedbacks
- **Apprendre** des erreurs ensemble
- **Promouvoir** un environnement inclusif

### ✅ Comportements attendus

- Utiliser un langage accueillant et inclusif
- Respecter les différents points de vue
- Accepter les critiques constructives avec grâce
- Se concentrer sur l'amélioration du projet
- Faire preuve d'empathie envers les autres

### ❌ Comportements inacceptables

- Langage ou imagerie sexualisés
- Commentaires insultants ou dégradants
- Harcèlement public ou privé
- Publication d'informations privées sans permission

## ❓ Aide et support

### 💬 Où obtenir de l'aide

1. **📖 Documentation** : [docs/](docs/)
2. **❓ FAQ** : [docs/faq.html](docs/faq.html)
3. **💬 Discussions** : [GitHub Discussions](https://github.com/your-repo/discussions)
4. **🐛 Issues** : Créez une issue pour les bugs confirmés
5. **💌 Email** : [support@enokdev.com](mailto:support@enokdev.com)

### 🕐 Temps de réponse

- **Issues critiques** : 24-48h
- **Bug reports** : 2-5 jours
- **Feature requests** : 1-2 semaines
- **Questions** : 1-3 jours
- **PR reviews** : 2-7 jours

## 🚀 Commencer maintenant

Prêt à contribuer ? Voici comment commencer :

1. **🍴 Fork** le repository
2. **📖 Lisez** la documentation
3. **🔍 Trouvez** une issue `good first issue`
4. **💬 Commentez** l'issue pour signaler votre intérêt
5. **🔧 Implémentez** votre solution
6. **📝 Créez** une Pull Request

### 🎯 Idées pour commencer

- **📖 Documentation** : Corriger des typos, améliorer des exemples
- **🧪 Tests** : Ajouter des tests manquants
- **🐛 Bugs** : Corriger des bugs simples
- **✨ Features** : Implémenter des fonctionnalités demandées
- **🔧 Outils** : Améliorer les scripts de build

---

**Merci de contribuer à Spring Boot GraphQL Auto-Generator ! 🚀**

Ensemble, nous construisons l'outil GraphQL le plus simple et efficace pour l'écosystème Spring Boot.

*Pour toute question sur ce guide, n'hésitez pas à ouvrir une issue ou contacter l'équipe à [contribute@enokdev.com](mailto:contribute@enokdev.com)*