# Fix the lopper openamp assist so it generates a correct split-mode dual-R5
# xlnx,zynqmp-r5fss for the Linux host DT. The stock assist (SRCREV 9159040)
# botches the second R5 core in split mode (truncated `ranges`, atcm1/btcm1
# reg-names, and dropped mboxes/carveouts on the 2nd rpmsg channel), which makes
# the kernel zynqmp_r5_remoteproc probe fail with "failed to get tcm resource"
# / err -22. See the patch header for the three specific fixes.
#
# NOTE: lopper-native is consumed by esw-conf-native (which gen-machineconf runs),
# so after adding this, rebuild both for the fix to reach gen-machineconf:
#   bitbake -c cleansstate lopper-native esw-conf-native
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-openamp_xlnx-fix-split-mode-dual-r5.patch"
