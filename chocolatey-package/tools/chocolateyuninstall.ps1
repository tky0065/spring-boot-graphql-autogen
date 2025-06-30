# GraphQL Auto-Generator Chocolatey Uninstall Script

$ErrorActionPreference = 'Stop'

$packageName = 'graphql-autogen'
$toolsDir = "$(Split-Path -parent $MyInvocation.MyCommand.Definition)"
$installDir = Join-Path $toolsDir 'graphql-autogen'

Write-Host "Uninstalling GraphQL Auto-Generator CLI..." -ForegroundColor Yellow

# Remove from PATH
try {
    # Remove the shim
    if (Get-Command "Uninstall-BinFile" -ErrorAction SilentlyContinue) {
        Uninstall-BinFile -Name 'graphql-autogen'
    }
    
    # Remove from Chocolatey bin directory
    $binDir = Join-Path $env:ChocolateyInstall 'bin'
    $binFile = Join-Path $binDir 'graphql-autogen.bat'
    if (Test-Path $binFile) {
        Remove-Item $binFile -Force
        Write-Host "Removed batch file from Chocolatey bin directory" -ForegroundColor Green
    }
} catch {
    Write-Warning "Could not remove CLI from PATH: $_"
}

# Remove installation directory
if (Test-Path $installDir) {
    try {
        Remove-Item $installDir -Recurse -Force
        Write-Host "Removed installation directory: $installDir" -ForegroundColor Green
    } catch {
        Write-Warning "Could not remove installation directory: $_"
        Write-Host "You may need to manually remove: $installDir" -ForegroundColor Yellow
    }
}

# Clean up any remaining files in tools directory
$filesToRemove = @(
    'graphql-autogen-*.zip',
    'graphql-autogen-*.jar',
    'graphql-autogen.bat',
    'graphql-autogen.ps1'
)

foreach ($pattern in $filesToRemove) {
    $files = Get-ChildItem -Path $toolsDir -Filter $pattern -ErrorAction SilentlyContinue
    foreach ($file in $files) {
        try {
            Remove-Item $file.FullName -Force
            Write-Host "Removed: $($file.Name)" -ForegroundColor Green
        } catch {
            Write-Warning "Could not remove $($file.Name): $_"
        }
    }
}

Write-Host ""
Write-Host "GraphQL Auto-Generator CLI has been uninstalled successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Thank you for using GraphQL Auto-Generator! 🚀" -ForegroundColor Cyan
Write-Host ""
Write-Host "If you encounter any issues:" -ForegroundColor Yellow
Write-Host "- GitHub Issues: https://github.com/your-org/spring-boot-graphql-autogen/issues"
Write-Host "- Documentation: https://your-domain.github.io/spring-boot-graphql-autogen/"
Write-Host "- Email: support@enokdev.com"
Write-Host ""
