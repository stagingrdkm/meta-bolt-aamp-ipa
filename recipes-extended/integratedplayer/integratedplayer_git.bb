SUMMARY = "Integrted player for RDK-E"
DESCRIPTION = "A reference integrated player for RDK-E"
HOMEPAGE = "https://github.com/rdkcentral/feature-test-tools"
SECTION = "apps"

LICENSE = "Apache License 2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

PV = "1.0"

SRC_URI = "git://github.com/rdkcentral/feature-test-tools;nobranch=1;protocol=https"
SRCREV = "02ab1cb5e07dafcb68ca43c45dc7b74a0e786daa"

S = "${WORKDIR}/git/IntegratedPlayer"
DEPENDS = "rpcserver aamp gstreamer1.0 jsoncpp"
RDEPENDS_${PN} = "rpcserver aamp"
DEPENDS += " glib-2.0"

inherit cmake pkgconfig

EXTRA_OECMAKE += " "

