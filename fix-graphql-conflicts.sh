#!/bin/bash

# Script pour corriger les conflits de noms GraphQLType
echo "🔧 Correction des conflits GraphQLType..."

# Répertoire racine du projet
PROJECT_ROOT="."

# Fonction pour corriger un fichier
fix_file() {
    local file="$1"
    echo "Correction de $file..."
    
    # Créer une sauvegarde
    cp "$file" "$file.backup"
    
    # Remplacements spécifiques
    sed -i '' 's/Map<Class<?>, GraphQLType>/Map<Class<?>, graphql.schema.GraphQLType>/g' "$file"
    sed -i '' 's/public GraphQLType resolveType/public graphql.schema.GraphQLType resolveType/g' "$file"
    sed -i '' 's/private GraphQLType doResolveType/private graphql.schema.GraphQLType doResolveType/g' "$file"
    sed -i '' 's/private GraphQLType resolveParameterizedType/private graphql.schema.GraphQLType resolveParameterizedType/g' "$file"
    sed -i '' 's/GraphQLType cachedType/graphql.schema.GraphQLType cachedType/g' "$file"
    sed -i '' 's/GraphQLType resolvedType/graphql.schema.GraphQLType resolvedType/g' "$file"
    sed -i '' 's/GraphQLType elementType/graphql.schema.GraphQLType elementType/g' "$file"
    
    # Remplacements pour les tests
    sed -i '' 's/import graphql.schema.GraphQLType;/\/\/ import graphql.schema.GraphQLType; \/\/ Commented to avoid conflicts/g' "$file"
    sed -i '' 's/GraphQLType type/graphql.schema.GraphQLType type/g' "$file"
    sed -i '' 's/GraphQLArgument arg/graphql.schema.GraphQLArgument arg/g' "$file"
    sed -i '' 's/GraphQLArgument\.newArgument/graphql.schema.GraphQLArgument.newArgument/g' "$file"
    
    echo "✅ $file corrigé"
}

# Liste des fichiers à corriger
files=(
    "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/generator/DefaultTypeResolver.java"
    "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/generator/TypeResolver.java"
    "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/generator/DefaultSchemaGenerator.java"
    "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/generator/DefaultFieldResolver.java"
    "graphql-autogen-core/src/test/java/com/enokdev/graphql/autogen/generator/DefaultTypeResolverTest.java"
    "graphql-autogen-core/src/test/java/com/enokdev/graphql/autogen/generator/DefaultOperationResolverTest.java"
    "graphql-autogen-core/src/test/java/com/enokdev/graphql/autogen/integration/OperationGenerationIntegrationTest.java"
    "graphql-autogen-core/src/test/java/com/enokdev/graphql/autogen/integration/TypeGenerationIntegrationTest.java"
)

# Corriger chaque fichier
for file in "${files[@]}"; do
    if [[ -f "$file" ]]; then
        fix_file "$file"
    else
        echo "⚠️  Fichier non trouvé: $file"
    fi
done

echo "🎉 Correction terminée!"
echo "📝 Des sauvegardes ont été créées avec l'extension .backup"
echo "🏗️  Vous pouvez maintenant relancer le build avec ./scripts/build-all.sh"
