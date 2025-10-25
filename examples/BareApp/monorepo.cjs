const fs = require('fs');
const path = require('path');
const pkg = require('./package.json');

class Monorepo {
  /**
   * Absolute path to monorepo root.
   * @type {string}
   */
  static root = path.resolve(__dirname, '../..');

  /**
   * Path to packages directory.
   * @type {string}
   */
  static packagesDir = path.join(Monorepo.root, 'packages');

  /**
   * Node Modules Paths.
   * @type {Array<string>}
   */
  static nodeModulesPaths = [
    path.join(__dirname, 'node_modules'),
    path.join(Monorepo.root, 'node_modules'),
  ];

  /**
   * Packages declared in current project's package.json (workspace references).
   * @type {Array<string>}
   */
  static workspaceDeps = Object.entries(pkg.dependencies || {})
    .filter(([, version]) => version === 'workspace:*')
    .map(([name]) => name);

  /**
   * Finds the absolute path for a given package name by reading all package.json files.
   * @param {string} pkgName
   * @returns {string | null} Path to the package root, or null if not found.
   */
  static resolvePackagePath(pkgName) {
    if (!fs.existsSync(this.packagesDir)) return null;

    const dirs = fs
      .readdirSync(this.packagesDir)
      .filter((name) =>
        fs.statSync(path.join(this.packagesDir, name)).isDirectory()
      );

    for (const dir of dirs) {
      const pkgPath = path.join(this.packagesDir, dir, 'package.json');
      if (!fs.existsSync(pkgPath)) continue;

      const pkg = JSON.parse(fs.readFileSync(pkgPath, 'utf-8'));
      if (pkg.name === pkgName) {
        return path.join(this.packagesDir, dir);
      }
    }

    return null;
  }

  /**
   * Builds an alias map for Metro bundler.
   * @returns {Record<string, string>}
   */
  static buildAliases() {
    const aliases = {};
    for (const pkgName of this.workspaceDeps) {
      const pkgDir = this.resolvePackagePath(pkgName);
      if (pkgDir) aliases[pkgName] = pkgDir;
    }
    return aliases;
  }

  /**
   * Builds dependency entries for `react-native.config.js`.
   * @returns {Record<string, { root: string, platforms: { ios: object, android: object } }>}
   */
  static buildDependencies() {
    const deps = {};
    for (const pkgName of this.workspaceDeps) {
      const pkgDir = this.resolvePackagePath(pkgName);
      if (pkgDir) {
        deps[pkgName] = {
          root: pkgDir,
          platforms: { ios: {}, android: {} },
        };
      }
    }
    return deps;
  }
}

module.exports = { Monorepo };
