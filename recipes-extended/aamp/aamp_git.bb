SUMMARY = "RDK AAMP component recipe specific for pure RDK IPA PoC purpose"
#Deliberatly for this particular usecase PoC, signifcantly reduced/modified/simplified the recipe compared to original all cases recipe from https://github.com/rdkcentral/meta-rdk-video/blob/develop/recipes-extended/aamp/aamp_git.bb
SECTION = "console/utils"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97dd37dbf35103376811825b038fc32b"

PV = "3.3.0"
PR = "r0"

SRCREV_FORMAT = "aamp"
SRCREV_aamp ?= "32ffcb2f9ba33838d243954abca53a9195cc2762"

# Support to build from a different branch by overriding both AAMP_BRANCH and SRCREV_aamp to specific branch and revision.
AAMP_BRANCH ?= "develop"
CMF_GITHUB_BRANCH = "branch=${AAMP_BRANCH}"

# original: DEPENDS += "curl libdash libxml2 cjson readline ${@bb.utils.contains('DISTRO_FEATURES', 'build_external_player_interface', 'player-interface', '', d)} ${@bb.utils.contains('DISTRO_FEATURES', 'webkitbrowser-plugin', '${WPEWEBKIT}', '', d)} ${@bb.utils.contains('DISTRO_FEATURES', 'subtec', 'closedcaption-hal-headers virtual/vendor-dvb virtual/vendor-closedcaption-hal', '', d)} ${@bb.utils.contains('DISTRO_FEATURES', 'enable_rialto', 'dobby', '', d)}"
# replaced with this line, note I had to add gstreamer1.0 gstreamer1.0-plugins-base
DEPENDS += "curl libdash libxml2 cjson readline gstreamer1.0 gstreamer1.0-plugins-base"

# original: RDEPENDS:${PN} += "devicesettings ${@bb.utils.contains('DISTRO_FEATURES', 'build_external_player_interface', 'player-interface', '', d)} ${@bb.utils.contains('DISTRO_FEATURES', 'subtec', 'packagegroup-subttxrend-app', '', d)}"
# replaced with this line
# RDEPENDS:${PN} += ""

inherit pkgconfig
inherit cmake
# original: require ${@bb.utils.contains('DISTRO_FEATURES', 'build_external_player_interface', '', 'aamp-middleware.inc', d)}

# original: EXTRA_OECMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'build_external_player_interface', ' -DCMAKE_EXTERNAL_PLAYER_INTERFACE_DEPENDENCIES=1', ' -DCMAKE_EXTERNAL_PLAYER_INTERFACE_DEPENDENCIES=0', d)}"
# replacing with
# this CMAKE_EXTERNAL_PLAYER_INTERFACE_DEPENDENCIES basically switches between either building those component source from dedicated repo/recipe or building them from within aamp sources but in both cases they will still be build and bring in big dependency tree
# At first look that depedency tree could be problematic, need to further analyse
# Putting this to 0 will choose to build them for aamp sources and drags them in here 
# https://github.com/rdkcentral/aamp/blob/develop/CMakeLists.txt#L242-L255 
EXTRA_OECMAKE += " -DCMAKE_EXTERNAL_PLAYER_INTERFACE_DEPENDENCIES=0"

NO_RECOMMENDATIONS = "1"

SRC_URI = "git://github.com/rdkcentral/aamp.git;protocol=https;branch=develop;name=aamp"
#adding text based aamp config file read by aamp at launchtime to be installed in /opt/aamp.cfg
SRC_URI += "file://aamp.cfg"

S = "${WORKDIR}/git"

# original: require aamp-common.inc
# replcace with adding relevant compile settings directly instead of .inc
EXTRA_OECMAKE += " -DCMAKE_SYSTEMD_JOURNAL=0"
EXTRA_OECMAKE += " -DCMAKE_BUILD_TYPE=Debug"
# has impact here https://github.com/rdkcentral/aamp/blob/develop/CMakeLists.txt#L260
EXTRA_OECMAKE += " -DCMAKE_INBUILT_AAMP_DEPENDENCIES=1"

# original: require aamp-artifacts-version.inc
#remove the whole widget artifact generation, here we are making bolt package

# original: EXTRA_OECMAKE += " -DCMAKE_DS_EVENT_SUPPORTED=1 "
# original: EXTRA_OECMAKE += " -DCMAKE_WPEWEBKIT_WATERMARK_JSBINDINGS=1 "
# replace with
EXTRA_OECMAKE += " -DCMAKE_WPEWEBKIT_WATERMARK_JSBINDINGS=0 "

#Ethan log is implemented by Dobby hence enabling it.
PACKAGES = "${PN} ${PN}-dev ${PN}-dbg"

FILES:${PN} += "${libdir}/lib*.so"
FILES:${PN} += "${libdir}/aamp-cli"
FILES:${PN} += "${libdir}/aamp/lib*.so"
FILES:${PN} +="${libdir}/gstreamer-1.0/lib*.so"
FILES:${PN}-dbg +="${libdir}/gstreamer-1.0/.debug/*"
#added for dev purpose
FILES:${PN} += "/opt /opt/aamp.cfg"
#
INSANE_SKIP:${PN} = "dev-so"

# original: required for specific products but for now distro is available only for UK 
# replaced by commenting out
#CXXFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'RDKE_REGION_UK', ' -DENABLE_USE_SINGLE_PIPELINE=1', '', d)}"
#CXXFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'RDKE_REGION_IT', ' -DENABLE_USE_SINGLE_PIPELINE=1', '', d)}"
#CXXFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'RDKE_REGION_DE', ' -DENABLE_USE_SINGLE_PIPELINE=1', '', d)}"
#CXXFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'RDKE_REGION_AU', ' -DENABLE_USE_SINGLE_PIPELINE=1', '', d)}"

# orignal: Enable PTS restamp feature
# replaced by commention out
#CXXFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'RDKE_REGION_UK', ' -DENABLE_PTS_RESTAMP=1', '', d)}"
#CXXFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'RDKE_REGION_IT', ' -DENABLE_PTS_RESTAMP=1', '', d)}"
#CXXFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'RDKE_REGION_DE', ' -DENABLE_PTS_RESTAMP=1', '', d)}"
#CXXFLAGS:append = "${@bb.utils.contains('DISTRO_FEATURES', 'RDKE_REGION_AU', ' -DENABLE_PTS_RESTAMP=1', '', d)}"

# modified, commented out below, hope no such deps and drag in of ds code:
#INCLUDE_DIRS = " \
#    -I=${includedir}/rdk/halif/ds-hal \
#    "

do_install:append() {
    echo "Installing aamp-cli..."
    install -m755 ${B}/aamp-cli ${D}${libdir}

    # remove the static library if it is installed, 
    # CMakelist in aamp code installing static lib below line should avoid build error 
    rm -f ${D}${libdir}/libtsb.a
    # adding in this ipa develop stage the aamp config file in container rootfs, according to documenation needs to be in /opt/aamp.cfg
    install -d ${D}/opt
    install -m 0644 ${WORKDIR}/aamp.cfg ${D}/opt/aamp.cfg
}

# removing the whole artifact generation piece. Don't need it for our use case
# removed it from this file
#
