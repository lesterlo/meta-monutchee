SUMMARY = "KR260Demo dfx-mgr PL application (bitstream + overlay + shell.json) and R5 firmware"
DESCRIPTION = "Packages the KR260Demo programmable-logic full bitstream as \
KR260Demo_PL.bit.bin, its device-tree overlay (pl.dtbo) and an auto-generated \
shell.json into /lib/firmware/xilinx/kr260demo/ so the design is discoverable and \
loadable with 'xmutil loadapp kr260demo' (dfx-mgr applies the overlay, which brings \
up the PL clocks and the PS<->PL AXI bridge). The Cortex-R5 firmware R5c0.elf and \
R5c1.elf are installed into /lib/firmware for manual remoteproc loading; dfx-mgr / \
xmutil on the 2025.2 release does NOT load RPU firmware."

LICENSE = "CLOSED"

# dfx_user_dts is AMD/Xilinx's Kria-app packaging class: it bootgen-converts the
# .bit to a .bin, cpp+dtc-compiles the PL overlay (resolving &zynqmp_* labels via
# the kernel device-tree include path), and installs the app under
# /lib/firmware/xilinx/${FW_INSTALL_DIR}/. DEPENDS (dtc-native bootgen-native) and
# the virtual/kernel:do_configure compile dependency come from the class.
inherit dfx_user_dts

COMPATIBLE_MACHINE = "^kr260demo$"
PACKAGE_ARCH = "${MACHINE_ARCH}"

# dfx-mgr app/folder name -> "xmutil loadapp kr260demo".
FW_INSTALL_DIR = "kr260demo"

# Take the PL bitstream, the SDT-generated PL overlay source, and the R5 ELFs
# straight from the developer workspace (mirrors the zynqmp-firmware "local" flow).
# TOPDIR is <project>/yocto-build/build, so TOPDIR/../.. is the project root.
FILESEXTRAPATHS:prepend := "${TOPDIR}/../../runtime-generated/bin_file:${TOPDIR}/../../runtime-generated/vivado_SDT_out:${TOPDIR}/../../KR260Demo_RPU/R5c0/build:${TOPDIR}/../../KR260Demo_RPU/R5c1/build:"

# .bit + .dtsi -> class runs bootgen (bit->bin) and dtc (dtsi->dtbo).
# The .elf files are ignored by the class and handled in do_install:append.
SRC_URI = " \
    file://fpga.bit \
    file://pl.dtsi \
    file://R5c0.elf \
    file://R5c1.elf \
"

FW_PATH = "${D}${nonarch_base_libdir}/firmware/xilinx/${FW_INSTALL_DIR}"

# The SDT emits pl.dtsi as a bare root fragment (no /dts-v1/; /plugin/;) that
# references base-tree labels (&zynqmp_clk / &zynqmp_reset). Wrap it as a /plugin/
# overlay so dtc emits an applicable .dtbo (those labels become __fixups__, resolved
# against the base tree's __symbols__ when dfx-mgr applies the overlay), and point
# firmware-name at the bitstream filename we ship. Runs after the class's own
# do_configure (which has already rewritten firmware-name to "${PN}.bin").
fixup_pl_dtsi() {
    dtsi="${S}/pl.dtsi"
    if ! grep -q "^/plugin/;" "$dtsi"; then
        sed -i '1i /plugin/;' "$dtsi"
        sed -i '1i /dts-v1/;' "$dtsi"
    fi
    sed -i 's/firmware-name = "[^"]*"/firmware-name = "KR260Demo_PL.bit.bin"/' "$dtsi"
}
do_configure[postfuncs] += "fixup_pl_dtsi"

do_install:append() {
    # The class installs the bitstream as ${PN}.bin and the overlay as ${PN}.dtbo;
    # rename to the requested filenames. The overlay's firmware-name was set to
    # KR260Demo_PL.bit.bin above, so the bitstream MUST carry exactly that name for
    # dfx-mgr / fpga-region to find it.
    if [ -f ${FW_PATH}/${PN}.bin ]; then
        mv ${FW_PATH}/${PN}.bin ${FW_PATH}/KR260Demo_PL.bit.bin
    fi
    if [ -f ${FW_PATH}/${PN}.dtbo ]; then
        mv ${FW_PATH}/${PN}.dtbo ${FW_PATH}/pl.dtbo
    fi

    # Auto-generate the dfx-mgr manifest: plain full bitstream, no XRT, no DFX slots.
    cat > ${FW_PATH}/shell.json <<EOF
{
    "shell_type": "PL_FLAT",
    "num_pl_slots": 0,
    "num_aie_slots": 0
}
EOF

    # Cortex-R5 firmware into /lib/firmware for manual remoteproc loading:
    #   echo R5c0.elf > /sys/class/remoteproc/remoteproc0/firmware ; echo start > .../state
    # (dfx-mgr/xmutil does not load RPU on 2025.2.)
    install -Dm 0644 ${WORKDIR}/R5c0.elf ${D}${nonarch_base_libdir}/firmware/R5c0.elf
    install -Dm 0644 ${WORKDIR}/R5c1.elf ${D}${nonarch_base_libdir}/firmware/R5c1.elf
}

do_install[vardeps] += "FW_INSTALL_DIR PN"

# The class's FILES:${PN} already claims /lib/firmware/xilinx/${FW_INSTALL_DIR};
# add the two R5 ELFs we drop directly in /lib/firmware.
FILES:${PN} += " \
    ${nonarch_base_libdir}/firmware/R5c0.elf \
    ${nonarch_base_libdir}/firmware/R5c1.elf \
"

# Prebuilt R5 (armv7r) ELFs + a bitstream blob; skip host/target arch QA.
INSANE_SKIP:${PN} += "arch ldflags textrel file-rdeps"
