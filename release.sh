#!/bin/bash

NEW_VERSION="1.6.2b"

git checkout main
git pull origin main

git tag -a $NEW_VERSION -m "Release version $NEW_VERSION"

git push origin $NEW_VERSION

git checkout production
git pull origin production

git merge main -m "Release $NEW_VERSION"

git push origin production

echo "Release $NEW_VERSION completed and merged into production."
read -p "Press any key to continue... "
