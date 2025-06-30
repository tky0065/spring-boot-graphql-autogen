#!/bin/bash

echo "🔄 Renommage des annotations pour éviter les conflits..."

# Fonction pour remplacer dans un fichier
replace_in_file() {
    local file="$1"
    if [[ -f "$file" ]]; then
        echo "Traitement de $file..."
        
        # GraphQLType -> GraphQLObjectType
        sed -i '' 's/@GraphQLType/@GraphQLObjectType/g' "$file"
        sed -i '' 's/GraphQLType\.class/GraphQLObjectType.class/g' "$file"
        sed -i '' 's/com\.enokdev\.graphql\.autogen\.annotation\.GraphQLType/com.enokdev.graphql.autogen.annotation.GraphQLObjectType/g' "$file"
        sed -i '' 's/isAnnotationPresent(GraphQLType\.class)/isAnnotationPresent(GraphQLObjectType.class)/g' "$file"
        sed -i '' 's/getAnnotation(GraphQLType\.class)/getAnnotation(GraphQLObjectType.class)/g' "$file"
        
        # GraphQLArgument -> GraphQLParam  
        sed -i '' 's/@GraphQLArgument/@GraphQLParam/g' "$file"
        sed -i '' 's/GraphQLArgument\.class/GraphQLParam.class/g' "$file"
        
        # Dans les imports
        sed -i '' 's/import com\.enokdev\.graphql\.autogen\.annotation\.GraphQLType;/import com.enokdev.graphql.autogen.annotation.GraphQLObjectType;/g' "$file"
        sed -i '' 's/import com\.enokdev\.graphql\.autogen\.annotation\.GraphQLArgument;/import com.enokdev.graphql.autogen.annotation.GraphQLParam;/g' "$file"
        
        echo "✅ $file traité"
    else
        echo "⚠️  Fichier non trouvé: $file"
    fi
}

# Fichiers à traiter
files=(
    "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/generator/DefaultTypeResolver.java"
    "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/generator/DefaultSchemaGenerator.java"
    "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/generator/DefaultFieldResolver.java"
    "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/generator/DefaultOperationResolver.java"
    "graphql-autogen-core/src/test/java/com/enokdev/graphql/autogen/generator/DefaultTypeResolverTest.java"
    "graphql-autogen-core/src/test/java/com/enokdev/graphql/autogen/generator/DefaultOperationResolverTest.java"
    "graphql-autogen-core/src/test/java/com/enokdev/graphql/autogen/integration/OperationGenerationIntegrationTest.java"
    "graphql-autogen-core/src/test/java/com/enokdev/graphql/autogen/integration/TypeGenerationIntegrationTest.java"
)

# Traiter chaque fichier
for file in "${files[@]}"; do
    replace_in_file "$file"
done

# Renommer le fichier d'annotation lui-même
if [[ -f "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/annotation/GraphQLType.java" ]]; then
    echo "Renommage de GraphQLType.java -> GraphQLObjectType.java"
    mv "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/annotation/GraphQLType.java" \
       "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/annotation/GraphQLObjectType.java"
fi

# Renommer GraphQLArgument si nécessaire
if [[ -f "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/annotation/GraphQLArgument.java" ]]; then
    echo "Renommage de GraphQLArgument.java -> GraphQLParam.java"
    mv "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/annotation/GraphQLArgument.java" \
       "graphql-autogen-core/src/main/java/com/enokdev/graphql/autogen/annotation/GraphQLParam.java"
fi

echo "🎉 Renommage terminé!"
echo "🏗️  Vous pouvez maintenant relancer le build avec ./scripts/build-all.sh"
