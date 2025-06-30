// Script pour le Playground Interactif

// Exemples de code prédéfinis
const codeExamples = {
    entity: `import com.enokdev.graphql.autogen.annotation.GraphQLType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
@GraphQLType
public class Product {
    @Id @GeneratedValue
    private Long id;
    
    private String name;
    private String description;
    private Double price;
    
    @ManyToOne
    private Category category;
    
    // Getters et setters
}`,
    controller: `import com.enokdev.graphql.autogen.annotation.GraphQLQuery;
import com.enokdev.graphql.autogen.annotation.GraphQLMutation;
import com.enokdev.graphql.autogen.annotation.GraphQLArgument;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GraphQLQuery(name = "products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @GraphQLQuery(name = "product")
    public Optional<Product> getProductById(@GraphQLArgument Long id) {
        return productRepository.findById(id);
    }
    
    @GraphQLMutation
    public Product createProduct(@GraphQLArgument ProductInput input) {
        // Implémentation de création
    }
}`,
    dto: `import com.enokdev.graphql.autogen.annotation.GraphQLType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@GraphQLType(input = true)
public class ProductInput {
    @NotBlank(message = "Le nom ne peut pas être vide")
    private String name;
    
    private String description;
    
    @Min(value = 0, message = "Le prix doit être positif")
    private Double price;
    
    private Long categoryId;
    
    // Getters et setters
}`
};

// Exemples de schémas GraphQL générés correspondants
const schemaExamples = {
    entity: `type Product {
  id: ID!
  name: String
  description: String
  price: Float
  category: Category
}

type Category {
  id: ID!
  name: String
  products: [Product!]
}`,
    controller: `type Query {
  products: [Product!]!
  product(id: ID!): Product
}

type Mutation {
  createProduct(input: ProductInput!): Product!
}

type Product {
  id: ID!
  name: String
  description: String
  price: Float
  category: Category
}`,
    dto: `input ProductInput {
  name: String!
  description: String
  price: Float
  categoryId: ID
}`
};

// Attendre que le DOM soit chargé
document.addEventListener('DOMContentLoaded', function() {
    // Récupérer les éléments du DOM
    const javaInput = document.getElementById('java-input');
    const graphqlOutput = document.getElementById('graphql-output');
    const generateBtn = document.getElementById('generate-btn');
    const exampleBtns = document.querySelectorAll('.example-btn');

    // Vérifier que les éléments existent
    if (!javaInput || !graphqlOutput || !generateBtn) {
        console.error("Éléments du Playground non trouvés dans le DOM");
        return;
    }

    // Charger l'exemple d'entité par défaut
    javaInput.value = codeExamples.entity;

    // Ajouter des gestionnaires d'événements pour les boutons d'exemples
    exampleBtns.forEach(button => {
        button.addEventListener('click', function() {
            const example = this.getAttribute('data-example');
            if (codeExamples[example]) {
                javaInput.value = codeExamples[example];
            }
        });
    });

    // Ajouter un gestionnaire d'événements pour le bouton de génération
    generateBtn.addEventListener('click', function() {
        // Simuler la génération du schéma GraphQL
        let generatedSchema = "// Génération du schéma en cours...\n";

        // Déterminer le type d'exemple d'après le contenu
        let exampleType = 'entity'; // par défaut

        const code = javaInput.value;
        if (code.includes('@RestController') || code.includes('@GraphQLQuery')) {
            exampleType = 'controller';
        } else if (code.includes('@GraphQLType(input = true)')) {
            exampleType = 'dto';
        }

        // Afficher le schéma correspondant
        setTimeout(() => {
            graphqlOutput.innerHTML = schemaExamples[exampleType] || "// Aucun schéma correspondant trouvé";
            Prism.highlightElement(graphqlOutput);
        }, 500);
    });

    // Initialiser la coloration syntaxique
    if (window.Prism) {
        Prism.highlightElement(graphqlOutput);
    }
});
