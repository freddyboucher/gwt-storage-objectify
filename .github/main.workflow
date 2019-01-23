workflow "Build/Deploy" {
  on = "push"
  resolves = ["Google Cloud SDK deploy"]
}

action "Maven build" {
  uses = "xlui/action-maven-cli/jdk8@master"
  args = "clean install -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
}

action "github-actions" {
  needs = "Maven build"
  uses = "actions/bin/filter@master"
  args = "branch github-actions"
}

action "Google Cloud SDK auth" {
  uses = "freddyboucher/gcloud/auth@master"
  needs = "github-actions"
  secrets = ["GCLOUD_AUTH"]
}

action "Google Cloud SDK deploy" {
  uses = "freddyboucher/gcloud/cli@master"
  needs = "Google Cloud SDK auth"
  args = "--quiet --verbosity=warning --project=gwt-storage-objectify app deploy gwt-storage-objectify-server/target/gwt-storage-objectify-server-0.0.1-SNAPSHOT --version=github-actions --promote"
}