# Retained license-text provenance

Texts are retained verbatim from the sources below. Their presence does not relicense
other files or by itself establish all binary-distribution obligations. See
[the audit](../docs/LICENSING.md) and [component notices](../THIRD_PARTY_NOTICES.md).

| Files | Source |
|---|---|
| `Apache-2.0.txt`, `LGPL-2.1.txt` | Existing upstream repository license texts, retained |
| `OFL-1.1-Libron.txt`, `Libron-COPYRIGHT.txt` | [Libron v0.25](https://github.com/nicoverbruggen/libron/tree/v0.25), `LICENSE` and `COPYRIGHT` |
| `OFL-1.1-Anton.txt`, `OFL-1.1-Archivo.txt` | Historical upstream font attribution; fonts no longer bundled |
| `AGPL-3.0.txt` | [GNU's official license text](https://www.gnu.org/licenses/agpl-3.0.txt) |
| `commons-compress-1.27.1-*` | `META-INF/LICENSE.txt` and `NOTICE.txt` from the resolved Maven JAR |
| `commons-codec-1.17.1-*` | Same entries from the resolved Maven JAR |
| `commons-io-2.16.1-*` | Same entries from the resolved Maven JAR |
| `commons-lang3-3.16.0-*` | Same entries from the resolved Maven JAR |
| `litert-1.4.2-LICENSE`, `litert-api-1.4.2-LICENSE` | Root `LICENSE` from the respective resolved Google Maven AARs |
| `SevenZip-JBinding-NOTICE.txt` | Root `License.txt` from the pinned native-library repository |
| `SevenZip-JBinding-License.txt`, `SevenZip-JBinding-AUTHORS.txt` | `sevenzipjbinding/src/main/cpp/License.txt` and `AUTHORS` at the pinned tag |
| `SevenZip-NOTICE.txt`, `unRAR-License.txt` | `sevenzipjbinding/src/main/cpp/7zip/DOC/License.txt` and `unRarLicense.txt` at the pinned tag |
| `p7zip-NOTICE.txt` | `sevenzipjbinding/src/main/cpp/p7zip/DOC/License.txt` at the pinned tag |
| `LZHAM-License.txt` | `sevenzipjbinding/src/main/cpp/p7zip/CPP/7zip/Compress/Lzham/LICENSE` at the pinned tag |

Pinned native-library source:
<https://github.com/omicronapps/7-Zip-JBinding-4Android/tree/Release-16.02-2.03>.

Runtime artifact texts were extracted from the Gradle-resolved versions listed here,
without modifying their copyright notices or license wording. Historical font notices
are deliberately preserved rather than silently deleted. All these files are copied
into APK assets by the build.
