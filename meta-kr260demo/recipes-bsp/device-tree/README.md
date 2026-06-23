Please use the Xilinx SDT workflow.

## SDT gen


```bash
echo 'set_dt_param \
   -xsa ../../bin_file/KR260Demo_PL.xsa\
   -dir ../../runtime-generated/vivado_SDT_out/ ; \
   generate_sdt ; exit' | sdtgen

   # -include_dts <path>/custom.dtsi # add this line if you
```
## gen-machine-conf

```bash
#Assume your are in yocto-build/build
gen-machineconf parse-sdt \
     --hw-description ../../runtime-generated/vivado_SDT_out/ \
     -c conf -D \
     -g full \
     --machine-name "kr260demo" \
     --add-config CONFIG_YOCTO_BBMC_CORTEXR5_0_FREERTOS=y \
     --add-config CONFIG_YOCTO_BBMC_CORTEXR5_1_FREERTOS=y \
     --require-machine include/k26-smk-kr.inc \
     --domain-file ../sources/meta-monutchee/meta-kr260demo/recipes-bsp/domainyaml/openamp-overlay-zynqmp.yaml
```

Use this [link](https://github.com/Xilinx/gen-machine-conf/blob/xilinx_v2025.2/gen-machine-scripts/configs/Kconfig.yoctomachinesettings)
to find your k26/k24 som or starter kit board name