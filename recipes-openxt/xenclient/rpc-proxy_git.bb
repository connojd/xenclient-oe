DESCRIPTION = "XenClient RPC proxy"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://../COPYING;md5=4641e94ec96f98fabc56ff9cc48be14b"
DEPENDS += " \
    libxchutils \
    libxchwebsocket \
    libxchv4v \
    libxchxenstore \
    libxch-rpc \
    libxchdb \
    hkg-dbus-core \
    hkg-json \
    hkg-hsyslog \
    hkg-network-bytestring \
    hkg-transformers \
    hkg-parsec \
    hkg-deepseq \
    hkg-text \
    hkg-mtl \
    hkg-network \
    hkg-monad-loops \
    hkg-lifted-base \
    hkg-monad-control \
    hkg-errors \
"
RDEPENDS_${PN} += "glibc-gconv-utf-32 bash"

PV = "0+git${SRCPV}"
SRCREV = "${AUTOREV}"
SRC_URI = " \
    git://${OPENXT_GIT_MIRROR}/manager.git;protocol=${OPENXT_GIT_PROTOCOL};branch=${OPENXT_BRANCH} \
    file://rpc-proxy.rules \
    file://rpc-proxy.initscript \
"

S = "${WORKDIR}/git/rpc-proxy"

HPV = "1.0"
require recipes-openxt/xclibs/xclibs.inc
inherit update-rc.d haskell xc-rpcgen-haskell-1.0

INITSCRIPT_NAME = "rpc-proxy"
INITSCRIPT_PARAMS = "defaults 30"

# ToDo: move xc-rpcgen into compile?

do_configure_append() {
	# generate rpc stubs
	mkdir -p Rpc/Autogen
	xc-rpcgen --haskell --templates-dir=${rpcgendatadir} -s -o Rpc/Autogen --module-prefix=Rpc.Autogen ${idldatadir}/rpc_proxy.xml
	xc-rpcgen --haskell --templates-dir=${rpcgendatadir} -c -o Rpc/Autogen --module-prefix=Rpc.Autogen ${idldatadir}/dbus.xml
}

do_install_append() {
	install -m 0755 -d ${D}/etc
	install -m 0755 -d ${D}/etc/init.d
	install -m 0644 ${WORKDIR}/rpc-proxy.rules ${D}/etc/rpc-proxy.rules
	install -m 0755 ${WORKDIR}/rpc-proxy.initscript ${D}${sysconfdir}/init.d/rpc-proxy
}
