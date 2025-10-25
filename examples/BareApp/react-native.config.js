const { Monorepo } = require('./monorepo.cjs');

module.exports = {
  project: {
    ios: {
      automaticPodsInstallation: true,
    },
  },
  dependencies: Monorepo.buildDependencies(),
};
