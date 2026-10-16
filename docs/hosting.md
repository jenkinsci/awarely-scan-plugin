# Jenkins project hosting readiness

Status: [hosting request #5383](https://github.com/jenkins-infra/repository-permissions-updater/issues/5383) was approved and completed on 2026-10-16. The canonical repository is **jenkinsci/awarely-scan-plugin**. [Release permissions PR #5388](https://github.com/jenkins-infra/repository-permissions-updater/pull/5388) is merged with CD enabled; the maintainer accepted the organization invitation and Jenkins-provisioned Maven credentials are present. Official Jenkins CI is active. The first official release, **18.v7f17b_86cf4b_8**, was published through Jenkins CD on 2026-10-16. Update Center and plugin-site indexing may follow later; historical GitHub 0.2.0 is not an Update Center release.

## Prepared in this repository

- Canonical repository: https://github.com/jenkinsci/awarely-scan-plugin
- Stable identity: `io.jenkins.plugins:awarely-scan`, name `Awarely Scan`, packaging `hpi`.
- Apache-2.0 license in `LICENSE` and `pom.xml`; existing dependency licenses retained.
- Plugin source, tests, English/Romanian how-to, security boundaries and contributor guide at the repository root.
- Root `Jenkinsfile` using `buildPlugin()` with Java 21/Linux.
- GitHub build checks for Linux amd64/arm64, secret scanning and Jenkins Security Scan (CodeQL).
- Incrementals extension/configuration and changelist-based POM versioning.
- Official Jenkins CD reusable workflow, with controlled activation below. Release notes are handled by that workflow; the redundant local Release Drafter configuration has been removed as requested by the hosting checker.
- [Submitted hosting request](hosting-request.md) with repository, identity, purpose and maintainer fields.

## Submission record

1. At submission, the [verified GitHub release](releases.md) was 0.2.0. It adds NuGet and Go checks through CLI v0.12.0 and the Monitor service, retaining job approval, credentials and policy boundaries. Manual acceptance and automated checks are recorded in the release documentation. Those GitHub releases were built and attested in the original awarelyeu repository; their preserved assets remain unchanged.
2. The owner confirmed the required initial Jira and Artifactory sign-ins for Jenkins community username `awarely_ro`, now recorded in the request. The GitHub maintainer is `awarelyeu`; GitHub and Jenkins usernames are independent.
3. Default-branch Build and Jenkins Security Scan passed for `a710386ffe72055d7e428819e8cdb811d4f27643`; the GitHub CodeQL alerts query reported no open alerts before submission. The request follows the [official hosting form](https://github.com/jenkins-infra/repository-permissions-updater/issues/new?template=1-hosting-request.yml), requests official GitHub-based releases, and asks the hosting team to coordinate repository transfer to preserve existing releases, assets and review history. Do not provide personal Maven tokens in the issue.

## Jenkins-controlled handoff and release activation

The first hosting-checker corrections configure `jenkins.baseline` for both core and the BOM, update the BOM to `7169.vb_32a_cfd93fa_6`, enable strict bundled-artifact and obsolete/deprecated dependency checks, and inherit Java configuration from the plugin parent. The SCM developer connection uses the Git SSH syntax supported by Jenkins's plugin compatibility tester. The minimum core remains **2.580.1**: on 2026-10-14 this is the first release of the active LTS line, while 2.580.3 is not published. Jenkins's baseline guidance recommends `.1` for the active LTS line and `.3` for historical lines. No controller upgrade is required for this metadata correction.

The POM, SCM links and CODEOWNERS now identify the approved Jenkins repository/team. Maintainer release permissions come from the hosting request rather than a POM `developers` block. Both public account reports now include `awarely_ro`; the hosting checker confirmed the request was in order.

The [hosting security scan](https://github.com/jenkins-infra/repository-permissions-updater/issues/5383#issuecomment-6172705261) raised two findings:

- The credential dropdown endpoint now requires POST, retaining its item-level `Item.CONFIGURE` authorization check. Jenkins's existing credentials selector already sends POST requests with a CSRF crumb. `AuthorityTest` exercises the HTTP endpoint and the authenticated configuration form, including rejection of GET and POST requests without a valid crumb and refusal to enumerate credentials for read-only users.
- `MonitorCredential.sourceKey` is a non-secret SHA-256 identity derived only from canonical API origin, application ID and source ID. It does not contain or hash the token. A field-specific `jenkins/plaintext-storage` suppression documents this false positive. `MonitorCredentialTest` checks its known digest, token rotation, canonical origin handling, persisted writer protection and token-free persisted metadata. The digest algorithm is unchanged so existing source bindings remain effective.

These changes are included in Jenkins-hosted **18.v7f17b_86cf4b_8**; they do not replace the historical 0.2.0 HPI. The [official re-scan passed](https://github.com/jenkins-infra/repository-permissions-updater/issues/5383#issuecomment-6203413705). Hosting was subsequently approved.

The hosting team forked the repository into `jenkinsci/awarely-scan-plugin`, granted maintainer access and arranged artifact permissions.

The POM `gitHubRepo`, active documentation, badges and reporting links now use `jenkinsci/awarely-scan-plugin`. All six historical GitHub releases are preserved there with identical assets and original provenance identity. Keep the original repository available as a read-only historical archive until the hosting team coordinates a fork-network handoff that preserves releases, review history and existing download URLs. Do not use GitHub's destructive detachment/deletion actions as a shortcut. The original scanner preview remains under `awarelyeu/awarely-sbom-scanner`.

The official CD workflow is already in `.github/workflows/cd.yml`. It:

- only accepts manual `workflow_dispatch` runs;
- defaults to `validate_only: true`;
- requires the exact canonical repository, `main`, and repository variable `JENKINS_CD_APPROVED=true`;
- uses only Jenkins-provisioned `MAVEN_USERNAME` and `MAVEN_TOKEN`;
- delegates CI-status verification and publication to the official Jenkins reusable workflow.

Hosting/CD review, the merged CD permissions and provisioned credentials satisfy the activation prerequisites. Enable `JENKINS_CD_APPROVED=true`, run validation first after the default-branch Jenkins build succeeds, and inspect the generated draft. A subsequent manual release run may clear `validate_only`. Never bypass the Jenkins CI verification. The POM already uses `${changelist}` with Jenkins's standard Incrementals configuration, so no runtime implementation rewrite is needed.

Jenkins-hosted releases go to the Jenkins Maven repository, from which update sites obtain plugins. After hosting, development previews should use Incrementals; the experimental update center is deprecated. The pre-hosting `preview-release.yml` is restricted to `awarelyeu/awarely-scan-plugin` and produces verified HPI downloads for manual staging tests. A GitHub alpha/beta release alone is not publication to Jenkins.

## Maintainer code review follow-up (2026-10-16)

Following [Markus Winter's review](https://github.com/jenkins-infra/repository-permissions-updater/issues/5383#issuecomment-6204719088):

- Removed the direct Structs dependency; core supplies `@Symbol`.
- Centralized fixed options and labels in enums and use `f:enum`. Existing String-based XML/Jenkinsfile values remain compatible through an explicit UI binding adapter; the credential selector remains a separately authorized POST endpoint.
- Moved CLI archive download to the controller with Jenkins' proxy-aware Java HttpClient builder, bounded response subscribers, an overall deadline and explicit HTTPS redirect checks. Only the verified public archive crosses Remoting. The agent verifies archive and executable digests again. The how-to distinguishes controller CLI download from agent-side Syft/Monitor traffic.
- Simplified the FilePath construction and made the global configuration page's ADMINISTER requirement explicit.
- Tested the actual `awarelyScan` step across a forcibly restarted controller, paused before agent/API access. The supported SimpleBuildStep/CoreStep Pipeline bridge fails active nonresumable execution without replay or normal downstream continuation. This is not durable-process support; the user guide states the limitation and uncertain-sync recovery.
- Retained release-authenticated tool pins, bounded no-follow/hardlink-rejecting reads, create-new private inventory copies, and ADMINISTER-only authority changes. Arbitrary tool manifests would change executable provenance; generic copy/containment helpers do not implement all the retained file checks. Added focused file and authority regressions rather than relaxing those controls.

These changes are included in Jenkins-hosted **18.v7f17b_86cf4b_8**. Existing signed GitHub release assets remain unchanged.

## Official references

- [Hosting process](https://www.jenkins.io/doc/developer/publishing/requesting-hosting/)
- [Preparation and account requirements](https://www.jenkins.io/doc/developer/publishing/preparation/)
- [Naming convention](https://www.jenkins.io/doc/developer/publishing/style-guides/)
- [Jenkins CI / buildPlugin](https://www.jenkins.io/doc/developer/publishing/continuous-integration/)
- [Jenkins Security Scan](https://www.jenkins.io/doc/developer/security/scan/)
- [Incrementals](https://www.jenkins.io/doc/developer/plugin-development/incrementals/)
- [Official CD setup and review](https://www.jenkins.io/doc/developer/publishing/releasing-cd/)
- [Experimental release status](https://www.jenkins.io/doc/developer/publishing/releasing-experimental-updates/)

## First official publication

- [Validation-only run](https://github.com/jenkinsci/awarely-scan-plugin/actions/runs/38977618980) passed before publishing.
- [Official release run](https://github.com/jenkinsci/awarely-scan-plugin/actions/runs/38977793596) published `18.v7f17b_86cf4b_8` from verified commit `7f17b86cf4b8b4cfa254176fd0fb9036bfa830ff`.
- The exact commit passed all 62 tests on ci.jenkins.io, SpotBugs and Jenkins Security Scan; no open CodeQL alerts were reported.
- The downloaded Maven HPI matched its repository SHA-256 and declared the expected plugin ID, version and Jenkins 2.580.1 minimum.
- The original repository is archived with a move notice. All six historical releases and 36 assets remain accessible in both repositories.
