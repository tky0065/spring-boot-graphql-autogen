# GraphQL Auto-Generator Homebrew Formula
# Installation : brew install graphql-autogen

class GraphqlAutogen < Formula
  desc "CLI tool for Spring Boot GraphQL Auto-Generator"
  homepage "https://your-domain.github.io/spring-boot-graphql-autogen/"
  version "1.0.0"
  
  if OS.mac?
    if Hardware::CPU.arm?
      url "https://github.com/your-org/spring-boot-graphql-autogen/releases/download/v#{version}/graphql-autogen-cli-#{version}-darwin-arm64.tar.gz"
      sha256 "sha256_checksum_here"
    else
      url "https://github.com/your-org/spring-boot-graphql-autogen/releases/download/v#{version}/graphql-autogen-cli-#{version}-darwin-x64.tar.gz"
      sha256 "sha256_checksum_here"
    end
  elsif OS.linux?
    url "https://github.com/your-org/spring-boot-graphql-autogen/releases/download/v#{version}/graphql-autogen-cli-#{version}-linux-x64.tar.gz"
    sha256 "sha256_checksum_here"
  end

  depends_on "openjdk@21"

  def install
    # Créer le répertoire de destination
    libexec.install Dir["*"]
    
    # Créer le script d'exécution
    (bin/"graphql-autogen").write <<~EOS
      #!/bin/bash
      export JAVA_HOME="#{Formula["openjdk@21"].opt_prefix}"
      exec "${JAVA_HOME}/bin/java" -jar "#{libexec}/graphql-autogen-cli-#{version}.jar" "$@"
    EOS
    
    # Rendre le script exécutable
    chmod 0755, bin/"graphql-autogen"
  end

  test do
    # Test de base pour vérifier que l'installation fonctionne
    system "#{bin}/graphql-autogen", "--version"
    assert_match "GraphQL Auto-Generator CLI v#{version}", shell_output("#{bin}/graphql-autogen --version")
    
    # Test de la commande help
    system "#{bin}/graphql-autogen", "--help"
  end

  def caveats
    <<~EOS
      GraphQL Auto-Generator CLI has been installed successfully!
      
      Usage:
        graphql-autogen generate --input src/main/java --output src/main/resources/graphql
        graphql-autogen validate --schema schema.graphqls
        graphql-autogen info --package com.example.model
        graphql-autogen init --type maven --name my-project
      
      For more information:
        graphql-autogen --help
        
      Documentation: https://your-domain.github.io/spring-boot-graphql-autogen/
      GitHub: https://github.com/your-org/spring-boot-graphql-autogen
    EOS
  end
end