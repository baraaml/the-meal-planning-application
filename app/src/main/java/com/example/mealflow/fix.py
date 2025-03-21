import os
import re

# Set the project root directory
project_root = os.path.dirname(os.path.abspath(__file__))

# Collect all Kotlin files in the 'ui' folder
ui_files = []
for root, dirs, files in os.walk(project_root):
    if 'ui' in root:  # Only consider files inside 'ui'
        for file in files:
            if file.endswith('.kt'):
                ui_files.append(os.path.join(root, file))

# Find broken imports in 'ui' files
def find_broken_imports():
    broken_imports = {}
    for file_path in ui_files:
        with open(file_path, 'r') as file:
            content = file.read()
            imports = re.findall(r'import\s+([a-zA-Z0-9._]+)', content)

            for imp in imports:
                # Check if the import exists in the 'ui' folder
                if not any(imp.replace('.', '/') in f for f in ui_files):
                    if file_path not in broken_imports:
                        broken_imports[file_path] = []
                    broken_imports[file_path].append(imp)

    return broken_imports

# Fix broken 'ui' imports smartly
def fix_ui_imports(file_path, broken_imports):
    with open(file_path, 'r') as file:
        content = file.read()

    for imp in broken_imports:
        if 'ui.' in imp:  # Focus only on 'ui' imports
            possible_paths = [f for f in ui_files if imp.split('.')[-1] in f]

            if possible_paths:
                possible_paths.sort(key=lambda x: x.count('/'))  # Closest match
                corrected_import = possible_paths[0].replace(project_root + '/', '').replace('/', '.').replace('.kt', '')
                content = re.sub(rf'import\s+{imp}', f'import {corrected_import}', content)

    with open(file_path, 'w') as file:
        file.write(content)

# Run the fixing process
broken_imports = find_broken_imports()
for file_path, imports in broken_imports.items():
    fix_ui_imports(file_path, imports)

print("UI import fixing completed. Please check your project files.")
