# Publishing

## Register on Sonatype

follow https://github.com/scalacenter/sbt-release-early/wiki/How-to-release-with-Sonatype#release-with-sonatype

You know have:

* SONATYPE_PASSWORD
* SONATYPE_USERNAME

```bash
gpg --version # make shure it's 1.X (not 2.X)
gpg --gen-key # with an empty passphrase
gpg --armor --export-secret-keys | base64 -w 0 | xclip -i # This is PGP_SECRET
gpg --list-keys

# For example
# /home/gui/.gnupg/pubring.gpg
# ----------------------------
# pub   2048R/6EBD580D 2017-12-04
# uid                  GMGMGM
# sub   2048R/135A5E9E 2017-12-04

gpg --keyserver hkp://pool.sks-keyservers.net --send-keys <YOUR KEY ID>

# For example: gpg --keyserver hkp://pool.sks-keyservers.net --send-keys 6EBD580D
```

Setup the project at https://travis-ci.org/scalacenter/versions/settings with

SONATYPE_PASSWORD
SONATYPE_USERNAME
PGP_PASSPHRASE (empty)
PGP_SECRET