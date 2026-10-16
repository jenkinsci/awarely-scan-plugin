# First Jenkins-hosted release: 18.v7f17b_86cf4b_8

The plugin is now maintained in `jenkinsci/awarely-scan-plugin` and uses the official Jenkins release workflow and version format. The plugin ID remains `awarely-scan`; existing installations can upgrade normally.

This release includes the hosting-review fixes after historical 0.2.0: POST/CSRF protection for credential selection, explicit ADMINISTER authority, proxy-aware bounded CLI downloads on the controller, compatible enum-backed configuration, documented controller-restart behavior and immediate persistence for global setters with one save per configuration form. The CLI remains v0.12.0 with managed Syft 1.54.1. Follow the English/Romanian guides for the distinct controller and agent network requirements.

The six earlier GitHub releases remain downloadable with their original provenance and checksums. They are historical artifacts, not newly built Jenkins releases.

# Awarely Scan for Jenkins 0.2.0

NuGet/.NET libraries and Go modules now support Monitor CVE checks through the existing Syft collector. The plugin pins CLI **v0.12.0** and managed Syft **1.54.1**. The collector label and help explain the preparation required, supported assessment and remaining coverage gaps.

NuGet and Go findings use the existing per-component version precision, severity gate, incomplete-assessment policy and build comparison. Backend checks require exact ecosystem/package identity and ecosystem-specific version ordering. Unknown versions and advisory syntax remain visible; Go toolchain/stdlib assessment is outside this module check. Job permissions, credential isolation, source ownership and tool verification remain enforced.

[English walkthrough](https://github.com/jenkinsci/awarely-scan-plugin/blob/main/docs/how-to.md#nugetnet-libraries-and-go-modules) · [Ghid în română](https://github.com/jenkinsci/awarely-scan-plugin/blob/main/docs/how-to.ro.md) · [Verify the release](https://github.com/jenkinsci/awarely-scan-plugin/blob/main/docs/releases.md)

This is the historical stable GitHub HPI release. Its assets are preserved unchanged after Jenkins hosting approval. See [releases](releases.md) for Jenkins-hosted versions.
