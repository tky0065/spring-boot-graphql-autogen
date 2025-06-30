#!/bin/bash

echo "🔧 Correction finale des conflits GraphQLObjectType..."

# Fonction pour remplacer dans un fichier
fix_file() {
    local file="$1"
    if [[ -f "$file" ]]; then
        echo "Traitement de $file..."
        
        # GraphQLObjectType -> AutoGenType (pour notre annotation)
        # Mais GraphQLObjectType reste pour les classes GraphQL-Java
        
        # Imports - remplacer seulement l'import de notre annotation
        sed -i '' 's/import com\.enokdev\.graphql\.autogen\.annotation\.GraphQLObjectType;/import com.enokdev.graphql.autogen.annotation.AutoGenType;/g' "$file"
        sed -i '' 's/import com\.enokdev\.graphql\.autogen\.annotation\.GraphQLParam;/import com.enokdev.graphql.autogen.annotation.AutoGenParam;/g' "$file"
        
        # Références à notre annotation (avec isAnnotationPresent)
        sed -i '' 's/isAnnotationPresent(GraphQLObjectType\.class)/isAnnotationPresent(AutoGenType.class)/g' "$file"
        sed -i '' 's/getAnnotation(GraphQLObjectType\.class)/getAnnotation(AutoGenType.class)/g' "$file"
        
        # GraphQLParam -> AutoGenParam
        sed -i '' 's/isAnnotationPresent(GraphQLParam\.class)/isAnnotationPresent(AutoGenParam.class)/g' "$file"
        sed -i '' 's/getAnnotation(GraphQLParam\.class)/getAnnotation(AutoGenParam.class)/g' "$file"
        
        # Dans les déclarations de variables où on veut notre annotation
        sed -i '' 's/AutoGenType annotation = javaClass\.getAnnotation(AutoGenType\.class);/AutoGenType annotation = javaClass.getAnnotation(AutoGenType.class);/g' "$file"
        
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
)

# Traiter chaque fichier
for file in "${files[@]}"; do
    fix_file "$file"
done

echo "🎉 Correction terminée!"
echo "🏗️  Relancez maintenant: mvn clean compile"
