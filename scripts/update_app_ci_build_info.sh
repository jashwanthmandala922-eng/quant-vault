# CI Build Info Updater
#
# Updates the ci.properties file with additional info from the CI build.
#
# Prerequisites:
#   - Git command line tools installed
#   - Write access to ci.properties file

if [ $# -ne 5 ]; then
    echo "Usage: $0 <repository> <branch> <commit_hash> <ci_run_number> <ci_run_attempt>"
    echo "E.g: ./scripts/$0 Quant Vault/android main abc123 123 1"
    exit 1
fi

set -euo pipefail

repository=$1
branch=$2
commit_hash=$3
ci_run_number=$4
ci_run_attempt=$5

ci_build_info_file="ci.properties"
git_source="${repository}/${branch}@${commit_hash}"
ci_run_source="${repository}/actions/runs/${ci_run_number}/attempts/${ci_run_attempt}"
emoji_brick="\\ud83e\\uddf1" # 🧱
emoji_computer="\\ud83d\\udcbb" # 💻

echo "🧱 Updating app CI Build info..."
echo "🧱 🧱 commit: ${git_source}"
echo "🧱 💻 build source: ${ci_run_source}"

cat << EOF > ${ci_build_info_file}
ci.info="${emoji_brick} commit: ${git_source}\\\n${emoji_computer} build source: ${ci_run_source}"
EOF

echo "✅ CI Build info updated successfully."
