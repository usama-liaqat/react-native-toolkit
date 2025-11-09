# 🧰 React Native Toolkit Monorepo

A collection of **modern, production-ready React Native libraries** built for scalability, performance, and developer experience — all under a unified toolkit.

This monorepo powers libraries like:

- [`@react-native-toolkit/cookies`](./packages/cookies) — Cookie Manager for React Native (iOS + Android)
- [`@react-native-toolkit/media-info`](./packages/media-info) — Extract and manage media metadata (e.g. duration, resolution, codec info)

Each package is independently versioned and published to npm, with full TypeScript support and native code integration.

---

## 📁 Repository Structure

```
.
├── packages/               # Source code for each React Native Toolkit package
│   ├── cookies/            # Cookie Manager (iOS + Android)
│   └── media-info/         # Media Info utility
│
├── examples/               # Example apps demonstrating package usage
│   └── BareApp/            # Minimal React Native example app
│
├── scripts/                # (Optional) Build or release scripts
├── tsconfig.json           # Shared TypeScript configuration
├── turbo.json              # Turborepo configuration
└── Makefile                # Common build/test tasks
```

---

## ⚙️ Development Setup

### 1️⃣ Install Dependencies

```bash
yarn install
```

### 2️⃣ Bootstrap Packages

```bash
yarn turbo run build
```

This will build all internal packages (`cookies`, `media-info`, etc.) and link them into the example app.

### 3️⃣ Run Example App

```bash
cd examples/BareApp
yarn start
```

Run on devices using:

```bash
yarn ios
# or
yarn android
```

---

## 🧱 Packages

| Package | Description | Version | Docs |
|----------|--------------|----------|------|
| [`@react-native-toolkit/cookies`](./packages/cookies) | Manage cookies on iOS & Android | — | [README](./packages/cookies/README.md) |
| [`@react-native-toolkit/media-info`](./packages/media-info) | Extract metadata from media files | — | [README](./packages/media-info/README.md) |

---

## 🧪 Testing

Each package contains its own Jest configuration.

To run all tests:

```bash
yarn test
```

Or run tests for a specific package:

```bash
yarn workspace @react-native-toolkit/cookies test
```

---

## 🚀 Publishing

This monorepo uses [Turborepo](https://turbo.build/repo) for task orchestration and can be published using standard npm/yarn workflows.

To publish a package manually:

```bash
cd packages/<package-name>
npm publish
```

---

## 🤝 Contributing

We welcome contributions!
Please read our [CONTRIBUTING.md](./CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md) before submitting a pull request.

---

## 📜 License

This project is licensed under the [MIT License](./LICENSE).

---

## 🧑‍💻 Maintainers

Developed and maintained by the **React Native Toolkit** team.
For issues, feature requests, or discussions — open a GitHub issue or reach out via [Discussions](https://github.com/your-repo/discussions).
