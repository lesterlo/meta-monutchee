export PRJ_NAME="mnc_build"

mkdir $PRJ_NAME && cd PRJ_NAME \
&& repo init -u https://github.com/lesterlo/mncux-manifest.git -b main && repo sync