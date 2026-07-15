SUMMARY = "Integrted player for RDK-E"
DESCRIPTION = "A reference integrated player for RDK-E"
HOMEPAGE = "https://github.com/rdkcentral/feature-test-tools"
SECTION = "apps"

LICENSE = "Apache License 2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

PV = "1.0"

SRC_URI = "git://github.com/rdkcentral/feature-test-tools;nobranch=1;protocol=https"
SRCREV = "a13e7e9d94040d719597255f453723683e4a7092"

S = "${WORKDIR}/git/IntegratedPlayer"
DEPENDS = "rpcserver aamp gstreamer1.0 jsoncpp glib-2.0"
RDEPENDS_${PN} = "rpcserver aamp"

inherit cmake pkgconfig

EXTRA_OECMAKE += " "

