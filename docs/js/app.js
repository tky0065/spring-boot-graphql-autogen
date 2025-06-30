// Spring Boot GraphQL Auto-Generator - Site Web Interactif
// Application JavaScript principale pour la documentation interactive

class GraphQLAutoGenSite {
    constructor() {
        this.initializeComponents();
        this.setupEventListeners();
        this.loadCodeExamples();
    }

    initializeComponents() {
        // Initialiser les composants de navigation
        this.setupNavigation();
        
        // Initialiser les onglets
        this.setupTabs();
        
        // Initialiser la coloration syntaxique
        this.setupCodeHighlighting();
        
        // Initialiser le playground interactif
        this.setupPlayground();
    }

    setupNavigation() {
        const nav = document.querySelector('.navbar');
        if (nav) {
            // Gestion du scroll pour la navbar
            window.addEventListener('scroll', () => {
                if (window.scrollY > 50) {
                    nav.classList.add('scrolled');
                } else {
                    nav.classList.remove('scrolled');
                }
            });
        }

        // Menu mobile
        const mobileToggle = document.querySelector('.mobile-menu-toggle');
        const mobileMenu = document.querySelector('.mobile-menu');
        
        if (mobileToggle && mobileMenu) {
            mobileToggle.addEventListener('click', () => {
                mobileMenu.classList.toggle('active');
            });
        }
    }

    setupTabs() {
        const tabButtons = document.querySelectorAll('.tab-button');
        const tabContents = document.querySelectorAll('.tab-content');

        tabButtons.forEach(button => {
            button.addEventListener('click', () => {
                const targetTab = button.dataset.tab;
                
                // Désactiver tous les onglets
                tabButtons.forEach(btn => btn.classList.remove('active'));
                tabContents.forEach(content => content.classList.remove('active'));
                
                // Activer l'onglet sélectionné
                button.classList.add('active');
                const targetContent = document.querySelector(`[data-tab-content="${targetTab}"]`);
                if (targetContent) {
                    targetContent.classList.add('active');
                }
            });
        });
    }

    setupCodeHighlighting() {
        // Utiliser Prism.js pour la coloration syntaxique
        if (typeof Prism !== 'undefined') {
            Prism.highlightAll();
        }

        // Ajouter des boutons de copie
        const codeBlocks = document.querySelectorAll('pre[class*="language-"]');
        codeBlocks.forEach(block => {
            const button = document.createElement('button');
            button.className = 'copy-button';
            button.innerHTML = '<i class="fas fa-copy"></i>';
            button.title = 'Copier le code';
            
            button.addEventListener('click', () => {
                const code = block.querySelector('code').textContent;
                navigator.clipboard.writeText(code).then(() => {
                    button.innerHTML = '<i class="fas fa-check"></i>';
                    setTimeout(() => {
                        button.innerHTML = '<i class="fas fa-copy"></i>';
                    }, 2000);
                });
            });
            
            block.style.position = 'relative';
            block.appendChild(button);
        });
    }

    setupPlayground() {
        const playgroundContainer = document.querySelector('#playground');
        if (!playgroundContainer) return;

        // Créer l'interface du playground
        playgroundContainer.innerHTML = `
            <div class="playground-header">
                <h3>🎮 Playground Interactif</h3>
                <p>Testez les annotations GraphQL Auto-Generator en temps réel</p>
            </div>
            
            <div class="playground-content">
                <div class="input-section">
                    <div class="input-header">
                        <h4>Code Java avec annotations</h4>
                        <div class="example-buttons">
                            <button class="example-btn" data-example="entity">Entité JPA</button>
                            <button class="example-btn" data-example="controller">Contrôleur</button>
                            <button class="example-btn" data-example="dto">DTO Input</button>
                        </div>
                    </div>
                    <textarea id="java-input" placeholder="Collez votre code Java ici..."></textarea>
                </div>
                
                <div class="output-section">
                    <div class="output-header">
                        <h4>Schéma GraphQL généré</h4>
                        <button id="generate-btn" class="btn-primary">Générer</button>
                    </div>
                    <pre id="graphql-output"><code class="language-graphql">// Le schéma GraphQL apparaîtra ici</code></pre>
                </div>
            </div>
        `;

        this.setupPlaygroundEvents();
        this.loadExamples();
    }

    setupPlaygroundEvents() {
        const generateBtn = document.querySelector('#generate-btn');
        const javaInput = document.querySelector('#java-input');
        const graphqlOutput = document.querySelector('#graphql-output code');
        const exampleButtons = document.querySelectorAll('.example-btn');

        if (generateBtn && javaInput && graphqlOutput) {
            generateBtn.addEventListener('click', () => {
                const javaCode = javaInput.value.trim();
                if (!javaCode) {
                    graphqlOutput.textContent = '// Veuillez entrer du code Java';
                    return;
                }

                // Simuler la génération du schéma GraphQL
                const generatedSchema = this.generateGraphQLSchema(javaCode);
                graphqlOutput.textContent = generatedSchema;
                
                // Re-highlight le code
                if (typeof Prism !== 'undefined') {
                    Prism.highlightElement(graphqlOutput);
                }
            });
        }

        // Gérer les exemples
        exampleButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const exampleType = btn.dataset.example;
                const example = this.getExample(exampleType);
                if (javaInput && example) {
                    javaInput.value = example;
                }
            });
        });
    }

    generateGraphQLSchema(javaCode) {
        // Simulation simple de génération de schéma
        // Dans la vraie version, cela appellerait l'API du générateur
        
        if (javaCode.includes('@GraphQLType')) {
            return `type Book {
    id: ID!
    title: String!
    author: Author
    publishedDate: DateTime
    status: BookStatus
}

type Author {
    id: ID!
    name: String!
    email: String
    books: [Book!]!
}

enum BookStatus {
    AVAILABLE
    BORROWED
    RESERVED
}

type Query {
    book(id: ID!): Book
    books(limit: Int = 10): [Book!]!
    searchBooks(query: String!): [Book!]!
}

type Mutation {
    createBook(input: CreateBookInput!): Book!
    updateBook(id: ID!, input: UpdateBookInput!): Book!
    deleteBook(id: ID!): Boolean!
}

input CreateBookInput {
    title: String!
    authorId: ID!
    publishedDate: DateTime
}

input UpdateBookInput {
    title: String
    publishedDate: DateTime
}

scalar DateTime`;
        }

        if (javaCode.includes('@GraphQLController')) {
            return `type Query {
    getAllBooks(limit: Int = 10, offset: Int = 0): [Book!]!
    getBook(id: ID!): Book
    searchBooks(query: String!): [Book!]!
}

type Mutation {
    createBook(input: CreateBookInput!): Book!
    updateBook(id: ID!, input: UpdateBookInput!): Book!
    deleteBook(id: ID!): Boolean!
}`;
        }

        return `# Schéma GraphQL généré automatiquement
# Ajoutez des annotations @GraphQLType, @GraphQLField, etc. pour voir le résultat`;
    }

    getExample(type) {
        const examples = {
            entity: `@Entity
@GraphQLType(name = "Book", description = "Représente un livre")
public class Book {
    @Id
    @GraphQLId
    private Long id;
    
    @GraphQLField(description = "Titre du livre", nullable = false)
    private String title;
    
    @GraphQLField(description = "Date de publication")
    private LocalDateTime publishedDate;
    
    @ManyToOne
    @GraphQLField
    private Author author;
    
    @Enumerated(EnumType.STRING)
    @GraphQLField
    private BookStatus status;
    
    @GraphQLIgnore
    private String internalNotes;
    
    // Getters et setters...
}`,
            
            controller: `@RestController
@GraphQLController(prefix = "book")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/books")
    @GraphQLQuery(name = "getAllBooks")
    public List<Book> getAllBooks(
        @RequestParam(defaultValue = "10") @GraphQLArgument Integer limit,
        @RequestParam(defaultValue = "0") @GraphQLArgument Integer offset
    ) {
        return bookService.findAll(limit, offset);
    }
    
    @GetMapping("/books/{id}")
    @GraphQLQuery(name = "getBook")
    public Book getBook(@PathVariable @GraphQLArgument(name = "id") Long id) {
        return bookService.findById(id);
    }
    
    @PostMapping("/books")
    @GraphQLMutation(name = "createBook")
    public Book createBook(@RequestBody @GraphQLArgument(name = "input") CreateBookDto input) {
        return bookService.create(input);
    }
}`,
            
            dto: `@GraphQLInput(name = "CreateBookInput")
public class CreateBookDto {
    
    @GraphQLInputField(required = true, description = "Titre du livre")
    @NotBlank
    private String title;
    
    @GraphQLInputField(description = "Description du livre")
    private String description;
    
    @GraphQLInputField(required = true, description = "ID de l'auteur")
    @NotNull
    private Long authorId;
    
    @GraphQLInputField(description = "Date de publication")
    private LocalDateTime publishedDate;
    
    @GraphQLInputField(description = "Statut du livre")
    private BookStatus status;
    
    // Getters et setters...
}`
        };

        return examples[type] || '';
    }

    loadCodeExamples() {
        // Charger et afficher des exemples de code dynamiquement
        const exampleContainers = document.querySelectorAll('[data-example-file]');
        
        exampleContainers.forEach(container => {
            const fileName = container.dataset.exampleFile;
            // Dans la vraie version, cela chargerait les fichiers depuis GitHub
            this.loadExampleFile(fileName, container);
        });
    }

    loadExampleFile(fileName, container) {
        // Simulation du chargement d'exemples
        const examples = {
            'BookEntity.java': `@Entity
@GraphQLType(name = "Book")
public class Book {
    @Id @GraphQLId
    private Long id;
    
    @GraphQLField(description = "Titre du livre")
    private String title;
    
    @ManyToOne @GraphQLField
    private Author author;
}`,
            'schema.graphqls': `type Book {
    id: ID!
    title: String!
    author: Author
}`
        };

        if (examples[fileName]) {
            container.innerHTML = `<pre><code class="language-java">${examples[fileName]}</code></pre>`;
            if (typeof Prism !== 'undefined') {
                Prism.highlightAllUnder(container);
            }
        }
    }
}

// Initialiser l'application quand le DOM est chargé
document.addEventListener('DOMContentLoaded', () => {
    new GraphQLAutoGenSite();
});

// Gestion du scroll smooth pour les liens d'ancrage
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Animation au scroll
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('animate-in');
        }
    });
}, observerOptions);

// Observer tous les éléments avec la classe 'fade-in'
document.querySelectorAll('.fade-in').forEach(el => {
    observer.observe(el);
});
