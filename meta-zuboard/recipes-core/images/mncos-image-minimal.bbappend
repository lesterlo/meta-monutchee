SUMMARY = "For ZUBoard with OpenAMP support"
DESCRIPTION = "An image including OpenAMP, libmetal, and device tree support for ZUBoard."

IMAGE_CLASSES:append: " export-tftpboot-file.bbclass"

# (Optional) Change destination directory on machine specific directory
# TFTPBOOT_DEST_DIR = "${TOPDIR}/export/tftpboot/${MACHINE}"