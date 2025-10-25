const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');
const { Monorepo } = require('./monorepo.cjs');

const config = {
  projectRoot: __dirname,
  watchFolders: [Monorepo.root, Monorepo.packagesDir],
  resolver: {
    nodeModulesPaths: Monorepo.nodeModulesPaths,
    extraNodeModules: Monorepo.buildAliases(),
  },
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
