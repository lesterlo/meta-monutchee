SUMMARY = "For ZUBoard with OpenAMP support"
DESCRIPTION = "An image including OpenAMP, libmetal, and device tree support for ZUBoard."

# DISTRO_FEATURES:append = " openamp"
IMAGE_INSTALL:append = " libmetal open-amp"


