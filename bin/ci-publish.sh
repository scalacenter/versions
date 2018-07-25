#!/usr/bin/env bash
set -eu

if [[ "$TRAVIS_SECURE_ENV_VARS" == true ]]; then
  echo "Publishing..."
  git log | head -n 20
  if [ -n "$TRAVIS_TAG" ]; then
    echo "$PGP_SECRET" | base64 --decode | gpg --import
    echo "Tag push, publishing release to Sonatype."
    sbt ";++$TRAVIS_SCALA_VERSION ;ci-release ;sonatypeReleaseAll"
  fi
else
  echo "Skipping publish, branch=$TRAVIS_BRANCH"
fi
