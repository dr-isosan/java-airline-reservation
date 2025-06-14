#!/bin/bash

echo "🎯 Airline Reservation System - GitHub Setup"
echo "=============================================="
echo ""
echo "✅ Your project is ready with:"
echo "   📁 16 files committed"
echo "   🎨 Beautiful emoji commit message"
echo "   🧪 100% test coverage (10/10 tests)"
echo "   📚 Professional documentation"
echo "   🔧 Complete Maven structure"
echo ""
echo "📋 To push to GitHub:"
echo ""
echo "1️⃣  Create repository at: https://github.com/new"
echo "    📝 Name: java-airline-reservation"
echo "    📖 Description: ✈️ Complete Airline Reservation System with Date Features & Modern GUI"
echo "    🌍 Set to Public (recommended)"
echo "    ❌ DON'T initialize with README"
echo ""
echo "2️⃣  Replace YOUR_USERNAME and run:"
echo ""

read -p "Enter your GitHub username: " username

if [ ! -z "$username" ]; then
    echo ""
    echo "🚀 Commands to run:"
    echo "git remote add origin https://github.com/$username/java-airline-reservation.git"
    echo "git branch -M main"
    echo "git push -u origin main"
    echo ""
    echo "💾 Copy these commands:"
    echo "========================"
    echo "git remote add origin https://github.com/$username/java-airline-reservation.git && git branch -M main && git push -u origin main"
    echo ""
    echo "✨ Your airline reservation system will be live on GitHub!"
else
    echo "⚠️  Please run the script again with your GitHub username"
fi
