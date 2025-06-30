# GraphQL Auto-Generator Chocolatey Install Script

$ErrorActionPreference = 'Stop'

# Package information
$packageName = 'graphql-autogen'
$packageVersion = '1.0.0'
$toolsDir = "$(Split-Path -parent $MyInvocation.MyCommand.Definition)"
$installDir = Join-Path $toolsDir 'graphql-autogen'

# Download URLs for different architectures
$url64 = "https://github.com/your-org/spring-boot-graphql-autogen/releases/download/v$packageVersion/graphql-autogen-cli-$packageVersion-windows-x64.zip"
$checksum64 = 'checksum_here'

# Package parameters
$packageArgs = @{
  packageName    = $packageName
  unzipLocation  = $toolsDir
  url64bit       = $url64
  checksum64     = $checksum64
  checksumType64 = 'sha256'
}

# Download and extract the package
Write-Host "Downloading GraphQL Auto-Generator CLI v$packageVersion..." -ForegroundColor Green
Install-ChocolateyZipPackage @packageArgs

# Create the installation directory structure
if (-not (Test-Path $installDir)) {
    New-Item -ItemType Directory -Path $installDir -Force | Out-Null
}

# Move files to proper location
$extractedDir = Join-Path $toolsDir "graphql-autogen-cli-$packageVersion"
if (Test-Path $extractedDir) {
    Get-ChildItem $extractedDir | Move-Item -Destination $installDir -Force
    Remove-Item $extractedDir -Recurse -Force
}

# Find the JAR file
$jarFile = Get-ChildItem -Path $installDir -Filter "*.jar" | Select-Object -First 1
if (-not $jarFile) {
    throw "Could not find GraphQL Auto-Generator JAR file in installation directory"
}

# Create batch script for CLI
$batchScript = @"
@echo off
setlocal

REM Check if JAVA_HOME is set
if not defined JAVA_HOME (
    echo Error: JAVA_HOME is not set. Please install Java 21 or higher.
    echo You can install it via: choco install openjdk21
    exit /b 1
)

REM Check Java version
for /f "tokens=3" %%g in ('"%JAVA_HOME%\bin\java" -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
)

REM Remove quotes from version
set JAVA_VERSION=%JAVA_VERSION:"=%

REM Run the CLI
"%JAVA_HOME%\bin\java" -jar "$installDir\$($jarFile.Name)" %*
"@

$batchFile = Join-Path $installDir 'graphql-autogen.bat'
$batchScript | Out-File -FilePath $batchFile -Encoding ASCII

# Create PowerShell script alternative
$psScript = @"
# GraphQL Auto-Generator PowerShell Wrapper
param(
    [Parameter(ValueFromRemainingArguments=`$true)]
    [string[]]`$Arguments
)

# Check if JAVA_HOME is set
if (-not `$env:JAVA_HOME) {
    Write-Error "JAVA_HOME is not set. Please install Java 21 or higher."
    Write-Host "You can install it via: choco install openjdk21" -ForegroundColor Yellow
    exit 1
}

# Check if Java executable exists
`$javaExe = Join-Path `$env:JAVA_HOME "bin\java.exe"
if (-not (Test-Path `$javaExe)) {
    Write-Error "Java executable not found at `$javaExe"
    exit 1
}

# Run the CLI
`$jarPath = "$installDir\$($jarFile.Name)"
& `$javaExe -jar `$jarPath @Arguments
"@

$psFile = Join-Path $installDir 'graphql-autogen.ps1'
$psScript | Out-File -FilePath $psFile -Encoding UTF8

# Add to PATH via shimgen
$shimPath = Join-Path $toolsDir 'graphql-autogen.exe'
Install-ChocolateyPath $installDir 'Machine'

# Create a shim for the batch file
if (Get-Command "Install-BinFile" -ErrorAction SilentlyContinue) {
    Install-BinFile -Name 'graphql-autogen' -Path $batchFile
} else {
    # Fallback: copy batch file to a location in PATH
    $binDir = Join-Path $env:ChocolateyInstall 'bin'
    if (Test-Path $binDir) {
        Copy-Item $batchFile -Destination (Join-Path $binDir 'graphql-autogen.bat') -Force
    }
}

# Set permissions
try {
    $acl = Get-Acl $installDir
    $accessRule = New-Object System.Security.AccessControl.FileSystemAccessRule("Users", "ReadAndExecute", "ContainerInherit,ObjectInherit", "None", "Allow")
    $acl.SetAccessRule($accessRule)
    Set-Acl $installDir $acl
} catch {
    Write-Warning "Could not set permissions on installation directory: $_"
}

Write-Host ""
Write-Host "GraphQL Auto-Generator CLI v$packageVersion has been installed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Usage:" -ForegroundColor Yellow
Write-Host "  graphql-autogen generate --input src/main/java --output src/main/resources/graphql"
Write-Host "  graphql-autogen validate --schema schema.graphqls"
Write-Host "  graphql-autogen info --package com.example.model"
Write-Host "  graphql-autogen init --type maven --name my-project"
Write-Host ""
Write-Host "For help:" -ForegroundColor Yellow
Write-Host "  graphql-autogen --help"
Write-Host ""
Write-Host "Documentation: https://your-domain.github.io/spring-boot-graphql-autogen/" -ForegroundColor Cyan
Write-Host "GitHub: https://github.com/your-org/spring-boot-graphql-autogen" -ForegroundColor Cyan
Write-Host ""

# Verify installation
try {
    if (Get-Command "java" -ErrorAction SilentlyContinue) {
        Write-Host "Testing installation..." -ForegroundColor Yellow
        $testResult = & $batchFile --version 2>&1
        if ($testResult -match "GraphQL Auto-Generator CLI") {
            Write-Host "✓ Installation verified successfully!" -ForegroundColor Green
        } else {
            Write-Warning "Installation test failed. You may need to restart your terminal or check your Java installation."
        }
    } else {
        Write-Warning "Java not found in PATH. Make sure Java 21+ is installed and JAVA_HOME is set."
        Write-Host "Install Java via: choco install openjdk21" -ForegroundColor Yellow
    }
} catch {
    Write-Warning "Could not verify installation: $_"
}
