##
# @file auto_clean_error.py
#
# @brief This script will have you to auto clean the old built package when you pulled branch
#
# @section description_doxygen_example Description
# Run: python3 auto_clean_error.py
# 
##


import subprocess
import re

def run_bitbake():
    # Run bitbake command and capture the output
    process = subprocess.Popen(["bitbake", "mncos-image-minimal"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    error_packages = set()
    
    output_lines = []
    for line in process.stdout:
        print(line, end="")  # Print the output for visibility
        output_lines.append(line)  # Store lines for post-processing

    # Wait for the process to finish
    process.wait()
    if process.returncode != 0:
        error_packages = parse_errors(output_lines)
    
    if error_packages:
        print("\nDetected error packages:", error_packages)
        clean_packages(error_packages)
    else:
        print("\nNo errors detected.")

def parse_errors(output_lines):
    error_packages = set()
    # Look for lines that indicate failed tasks in the "Summary" section
    for line in output_lines:
        # Match the package name before the version and revision
        match = re.search(r"ERROR: ([\w\-]+)-\d[\d\.]+-[\w\-]+", line)
        if match:
            error_packages.add(match.group(1))  # Extract package name before the version
    return error_packages

def clean_packages(packages):
    for package in packages:
        print(f"Cleaning package: {package}")
        result = subprocess.run(["bitbake", "-c", "clean", package], text=True)
        if result.returncode == 0:
            print(f"Successfully cleaned {package}")
        else:
            print(f"Failed to clean {package}, please check manually.")

if __name__ == "__main__":
    run_bitbake()
